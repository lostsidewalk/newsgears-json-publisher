package com.lostsidewalk.buffy.json;


import com.google.gson.JsonObject;
import com.lostsidewalk.buffy.DataAccessException;
import com.lostsidewalk.buffy.model.RenderedFeedDao;
import com.lostsidewalk.buffy.post.StagingPost;
import com.lostsidewalk.buffy.publisher.FeedPreview;
import com.lostsidewalk.buffy.publisher.Publisher;
import com.lostsidewalk.buffy.queue.QueueDefinition;
import com.lostsidewalk.buffy.queue.QueueDefinitionDao;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.lostsidewalk.buffy.publisher.Publisher.PubFormat.JSON;
import static java.time.Instant.now;
import static org.apache.commons.collections4.CollectionUtils.size;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Slf4j
@Component
public class JSONPublisher implements Publisher {

    @Autowired
    FeedObjectBuilder feedObjectBuilder;

    @Autowired
    PostsArrayBuilder postsArrayBuilder;

    @Autowired
    QueueDefinitionDao queueDefinitionDao;

    @Autowired
    RenderedFeedDao renderedFeedDao;

    @PostConstruct
    public void postConstruct() {
        log.info("JSON publisher constructed at {}", now());
    }

    @Override
    public PubResult publishFeed(QueueDefinition queueDefinition, List<StagingPost> stagingPosts, Date pubDate) {

        List<Throwable> errors = new ArrayList<>();
        String feedIdent = queueDefinition.getIdent();
        String transportIdent = queueDefinition.getTransportIdent();

        log.info("Deploying JSON feed with ident={}", feedIdent);

        try {
            String payload = buildPayload(queueDefinition, stagingPosts, pubDate).toString();
            renderedFeedDao.putJSONFeedAtTransportIdent(transportIdent, payload);
            log.info("Published JSON feed for feedIdent={}, transportIdent={}", feedIdent, transportIdent);
        } catch (Exception e) {
            errors.add(e);
        }

        return PubResult.from(getPublisherId(), errors, new Date());
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
    public List<FeedPreview> doPreview(String username, List<StagingPost> incomingPosts, PubFormat format) throws DataAccessException {
        log.info("JSON publisher has to {} posts to publish at {}", size(incomingPosts), now());
        // group posts by output file for tag
        Map<Long, List<StagingPost>> postsByFeedId = new HashMap<>();
        for (StagingPost incomingPost : incomingPosts) {
            postsByFeedId.computeIfAbsent(incomingPost.getQueueId(), t -> new ArrayList<>()).add(incomingPost);
        }
        List<FeedPreview> feedPreviews = new ArrayList<>(postsByFeedId.keySet().size());
        for (Map.Entry<Long, List<StagingPost>> e : postsByFeedId.entrySet()) {
            FeedPreview feedPreview = previewFeed(username, e.getKey(), e.getValue(), format);
            if (feedPreview != null) {
                feedPreviews.add(feedPreview);
            }
        }
        log.info("JSON publisher preview finished at {}", now());
        return feedPreviews;
    }

    FeedPreview previewFeed(String username, Long queueId, List<StagingPost> stagingPosts, PubFormat format) throws DataAccessException {
        log.info("Previewing feed with ident={}, format={}", (queueId == null ? "(all)" : queueId), format);
        String payload = EMPTY;
        QueueDefinition queueDefinitionDaoByFeedId = this.queueDefinitionDao.findByQueueId(username, queueId);
        if (queueDefinitionDaoByFeedId != null) {
            try {
                if (format == JSON) {
                    payload = buildPayload(queueDefinitionDaoByFeedId, stagingPosts, new Date()).toString();
                }
            } catch (Exception e) {
                log.error("Unable to rendered feed due to: {}", e.getMessage());
            }
        } else {
            log.warn("Unable to locate feed definition with Id={}", queueId);
        }

        payload = payload
                .replace("\n", EMPTY)
                .replace("\r", EMPTY);

        return FeedPreview.from(queueId, payload);
    }

    private static final String JSON_PUBLISHER_ID = "JSON";

    private JsonObject buildPayload(QueueDefinition queueDefinition, List<StagingPost> stagingPosts, Date pubDate) {
        // build/publish the feed
        JsonObject j = new JsonObject();
        j.add("feed", feedObjectBuilder.buildFeedObject(queueDefinition, pubDate));
        j.add("posts", postsArrayBuilder.buildPostsArray(stagingPosts));
        return j;
    }
}
