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

import static com.lostsidewalk.buffy.json.PostsArrayBuilder.buildPostsArray;
import static com.lostsidewalk.buffy.publisher.Publisher.PubFormat.JSON;
import static java.time.Instant.now;
import static org.apache.commons.collections4.CollectionUtils.size;
import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * The JSONPublisher class is responsible for publishing JSON feeds based on configuration properties and input staging posts.
 * It implements the {@link Publisher} interface to provide methods for publishing and previewing JSON feeds.
 */
@Slf4j
@Component
public class JSONPublisher implements Publisher {

    @Autowired
    JSONPublisherConfigProps configProps;

    @Autowired
    FeedObjectBuilder feedObjectBuilder;

    @Autowired
    QueueDefinitionDao queueDefinitionDao;

    @Autowired
    RenderedFeedDao renderedFeedDao;

    /**
     * Default constructor; initializes the object.
     */
    JSONPublisher() {
    }

    /**
     * Initializes the JSONPublisher bean after construction and logs the initialization timestamp.
     */
    @PostConstruct
    public static void postConstruct() {
        log.info("JSON publisher constructed at {}", now());
    }

    /**
     * Publishes a JSON feed based on the provided queue definition, staging posts, and publication date.
     *
     * @param queueDefinition The queue definition associated with the feed.
     * @param stagingPosts    The list of staging posts to include in the feed.
     * @param pubDate         The publication date of the feed.
     * @return A map containing the publication results with URLs and errors.
     */
    @Override
    public final Map<String, PubResult> publishFeed(QueueDefinition queueDefinition, List<StagingPost> stagingPosts, Date pubDate) {

        String transportPubUrl = null;
        String userIdentPubUrl = null;
        List<Throwable> errors = new ArrayList<>(1);
        String queueIdent = queueDefinition.getIdent();
        String transportIdent = queueDefinition.getTransportIdent();

        log.info("Deploying JSON feed with ident={}", queueIdent);

        try {
            String payload = buildPayload(queueDefinition, stagingPosts, pubDate).toString();
            renderedFeedDao.putJSONFeedAtTransportIdent(transportIdent, payload);
            transportPubUrl = String.format(configProps.getChannelLinkTemplate(), queueDefinition.getTransportIdent());
            userIdentPubUrl = String.format(configProps.getChannelLinkTemplate(), queueDefinition.getUsername() + "/" + queueIdent);
            log.info("Published JSON feed for queueIdent={}, transportIdent={}, transportPubUrl={}", queueIdent, transportIdent, transportPubUrl);
        } catch (DataAccessException | RuntimeException e) {
            errors.add(e);
        }

        return Map.of(JSON_PUBLISHER_ID, PubResult.from(transportPubUrl, userIdentPubUrl, errors, new Date()));
    }

    /**
     * Returns the publisher ID for this JSONPublisher.
     *
     * @return The publisher ID, which is "JSON".
     */
    @Override
    public final String getPublisherId() {
        return JSON_PUBLISHER_ID;
    }

    /**
     * Checks if this publisher supports the specified publication format (JSON).
     *
     * @param pubFormat The publication format to check.
     * @return true if the format is JSON, otherwise false.
     */
    @Override
    public final boolean supportsFormat(PubFormat pubFormat) {
        return pubFormat == JSON;
    }

    /**
     * Generates a preview of JSON feeds based on the provided username, staging posts, and publication format.
     *
     * @param username       The username associated with the feed.
     * @param incomingPosts  The list of incoming staging posts to include in the feed.
     * @param format         The publication format for the feed.
     * @return A list of feed previews.
     * @throws DataAccessException If there is an issue accessing data.
     */
    @Override
    public final List<FeedPreview> doPreview(String username, List<StagingPost> incomingPosts, PubFormat format) throws DataAccessException {
        log.info("JSON publisher has to {} posts to publish at {}", size(incomingPosts), now());
        // group posts by output file for tag
        Map<Long, List<StagingPost>> postsByFeedId = new HashMap<>(16);
        for (StagingPost incomingPost : incomingPosts) {
            postsByFeedId.computeIfAbsent(incomingPost.getQueueId(), t -> new ArrayList<>(size(incomingPosts))).add(incomingPost);
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

    private FeedPreview previewFeed(String username, Long queueId, Iterable<? extends StagingPost> stagingPosts, PubFormat format) throws DataAccessException {
        log.info("Previewing feed with ident={}, format={}", (queueId == null ? "(all)" : queueId), format);
        String payload = EMPTY;
        QueueDefinition queueDefinitionDaoByFeedId = queueDefinitionDao.findByQueueId(username, queueId);
        if (queueDefinitionDaoByFeedId != null) {
            try {
                if (format == JSON) {
                    payload = buildPayload(queueDefinitionDaoByFeedId, stagingPosts, new Date()).toString();
                }
            } catch (RuntimeException e) {
                log.error("Unable to rendered feed due to: {}", e.getMessage());
            }
        } else {
            log.warn("Unable to locate feed definition with Id={}", queueId);
        }

        //noinspection HardcodedLineSeparator
        payload = payload
                .replace("\n", EMPTY)
                .replace("\r", EMPTY);

        return FeedPreview.from(queueId, payload);
    }

    private JsonObject buildPayload(QueueDefinition queueDefinition, Iterable<? extends StagingPost> stagingPosts, Date pubDate) {
        // build/publish the feed
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("feed", feedObjectBuilder.buildFeedObject(queueDefinition, pubDate));
        jsonObject.add("posts", buildPostsArray(stagingPosts));
        return jsonObject;
    }

    private static final String JSON_PUBLISHER_ID = "JSON";

    @Override
    public final String toString() {
        return "JSONPublisher{" +
                "configProps=" + configProps +
                ", feedObjectBuilder=" + feedObjectBuilder +
                ", queueDefinitionDao=" + queueDefinitionDao +
                ", renderedFeedDao=" + renderedFeedDao +
                '}';
    }
}
