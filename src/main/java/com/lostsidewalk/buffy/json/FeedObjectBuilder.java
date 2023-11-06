package com.lostsidewalk.buffy.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lostsidewalk.buffy.queue.QueueDefinition;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.FastDateFormat;

import java.util.Date;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.time.FastDateFormat.MEDIUM;


@Slf4j
class FeedObjectBuilder {

    final JSONPublisherConfigProps configProps;

    FeedObjectBuilder(JSONPublisherConfigProps configProps) {
        this.configProps = configProps;
    }

    private static final FastDateFormat DATE_FORMATTER = FastDateFormat.getDateTimeInstance(MEDIUM, MEDIUM);

    private static final String IDENT_FIELD_NAME = "ident";

    private static final String TITLE_FIELD_NAME = "title";

    private static final String DESCRIPTION_FIELD_NAME = "description";

    private static final String GENERATOR_FIELD_NAME = "generator";

    private static final String COPYRIGHT_FIELD_NAME = "copyright";

    private static final String LANGUAGE_FIELD_NAME = "language";

    private static final String PUB_DATE_FIELD_NAME = "pubDate";

    private static final String URL_FIELD_NAME = "url";

    private static final String IMG_URL_FIELD_NAME = "imgUrl";

    final JsonElement buildFeedObject(QueueDefinition queueDefinition, Date pubDate) {
        JsonObject feedObject = new JsonObject();

        feedObject.addProperty(IDENT_FIELD_NAME, queueDefinition.getIdent());

        String title = queueDefinition.getTitle();
        if (isNotBlank(title)) {
            feedObject.addProperty(TITLE_FIELD_NAME, title);
        }

        String description = queueDefinition.getDescription();
        if (isNotBlank(description)) {
            feedObject.addProperty(DESCRIPTION_FIELD_NAME, description);
        }

        String generator = queueDefinition.getGenerator();
        if (isNotBlank(generator)) {
            feedObject.addProperty(GENERATOR_FIELD_NAME, generator);
        }

        String copyright = queueDefinition.getCopyright();
        if (isNotBlank(copyright)) {
            feedObject.addProperty(COPYRIGHT_FIELD_NAME, queueDefinition.getCopyright());
        }

        String language = queueDefinition.getLanguage();
        if (isNotBlank(language)) {
            feedObject.addProperty(LANGUAGE_FIELD_NAME, language);
        }

        feedObject.addProperty(PUB_DATE_FIELD_NAME, DATE_FORMATTER.format(pubDate));

        String url = String.format(configProps.getChannelLinkTemplate(), queueDefinition.getTransportIdent());
        feedObject.addProperty(URL_FIELD_NAME, url);

        String queueImgTransportIdent = queueDefinition.getQueueImgTransportIdent();
        if (isNotBlank(queueImgTransportIdent)) {
            String imgUrl = String.format(configProps.getChannelImageUrlTemplate(), queueImgTransportIdent);
            feedObject.addProperty(IMG_URL_FIELD_NAME, imgUrl);
        }

        return feedObject;
    }

    @Override
    public final String toString() {
        return "FeedObjectBuilder{" +
                "configProps=" + configProps +
                '}';
    }
}
