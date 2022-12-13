package com.lostsidewalk.buffy.json;


import com.google.gson.JsonObject;
import com.lostsidewalk.buffy.FeedPreview;
import com.lostsidewalk.buffy.Publisher;
import com.lostsidewalk.buffy.RenderedFeedDao;
import com.lostsidewalk.buffy.feed.FeedDefinition;
import com.lostsidewalk.buffy.feed.FeedDefinitionDao;
import com.lostsidewalk.buffy.post.StagingPost;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.lostsidewalk.buffy.Publisher.PubFormat.JSON;
import static java.time.Instant.now;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections4.CollectionUtils.size;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

@Slf4j
@Component
public class JSONPublisher implements Publisher {

    @Autowired
    FeedObjectBuilder feedObjectBuilder;

    @Autowired
    PostsArrayBuilder postsArrayBuilder;

    @Autowired
    FeedDefinitionDao feedDefinitionDao;

    @Autowired
    RenderedFeedDao renderedFeedDao;

    @PostConstruct
    public void postConstruct() {
        log.info("JSON publisher constructed at {}", now());
    }

    @Override
    public PubResult publishFeed(FeedDefinition feedDefinition, List<StagingPost> stagingPosts, Date pubDate) {

        List<Throwable> errors = new ArrayList<>();
        String feedIdent = feedDefinition.getIdent();
        String transportIdent = feedDefinition.getTransportIdent();

        log.info("Deploying JSON feed with ident={}", feedIdent);

        try {
            String payload = buildPayload(feedDefinition, stagingPosts, pubDate).toString();
            renderedFeedDao.putJSONFeedAtTransportIdent(transportIdent, payload);
            log.info("Published JSON feed for feedIdent={}, transportIdent={}", feedIdent, transportIdent);
        } catch (Exception e) {
            errors.add(e);
        }

        return PubResult.from(getPublisherId(), feedIdent, transportIdent, errors, new Date());
    }

    @Override
    public String getPublisherId() {
        return JSON_PUBLISHER_ID;
    }

    @Override
    public boolean supportsFormat(PubFormat pubFormat) {
        return pubFormat == JSON;
    }

    @Override
    public List<FeedPreview> doPreview(String username, List<StagingPost> incomingPosts, PubFormat format) {
        log.info("JSON publisher has to {} posts to publish at {}", size(incomingPosts), now());

        // group posts by output file for tag
        Map<String, List<StagingPost>> postsByFeedIdent = new HashMap<>();
        for (StagingPost incomingPost : incomingPosts) {
            postsByFeedIdent.computeIfAbsent(incomingPost.getFeedIdent(), t -> new ArrayList<>()).add(incomingPost);
        }

        List<FeedPreview> previewPosts = postsByFeedIdent.entrySet().stream()
                .map(e -> previewFeed(username, e.getKey(), e.getValue(), format))
                .filter(Objects::nonNull)
                .collect(toList());

        log.info("JSON publisher preview finished at {}", now());

        return previewPosts;
    }

    FeedPreview previewFeed(String username, String feedIdent, List<StagingPost> stagingPosts, PubFormat format) {
        log.info("Previewing feed with ident={}, format={}", defaultIfBlank(feedIdent, "(all)"), format);
        String payload = EMPTY;
        FeedDefinition feedDefinition = this.feedDefinitionDao.findByFeedIdent(username, feedIdent);
        if (feedDefinition != null) {
            try {
                if (format == JSON) {
                    payload = buildPayload(feedDefinition, stagingPosts, new Date()).toString();
                }
            } catch (Exception e) {
                log.error("Unable to rendered feed due to: {}", e.getMessage());
            }
        } else {
            log.warn("Unable to locate feed definition with ident={}", feedIdent);
        }

        payload = payload
                .replace("\n", EMPTY)
                .replace("\r", EMPTY);

        return FeedPreview.from(feedIdent, payload);
    }

    private static final String JSON_PUBLISHER_ID = "JSON";

    private JsonObject buildPayload(FeedDefinition feedDefinition, List<StagingPost> stagingPosts, Date pubDate) {
        // build/publish the feed
        JsonObject j = new JsonObject();
        j.add("feed", feedObjectBuilder.buildFeedObject(feedDefinition, pubDate));
        j.add("posts", postsArrayBuilder.buildPostsArray(stagingPosts));
        return j;
    }
}
