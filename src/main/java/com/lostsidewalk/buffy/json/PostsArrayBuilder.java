package com.lostsidewalk.buffy.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lostsidewalk.buffy.post.StagingPost;
import org.apache.commons.lang3.time.FastDateFormat;

import java.util.List;

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.time.FastDateFormat.MEDIUM;

class PostsArrayBuilder {

    private static final FastDateFormat DATE_FORMATTER = FastDateFormat.getDateTimeInstance(MEDIUM, MEDIUM);
    //
    //
    //
    private static final String POST_TITLE_FIELD_NAME = "postTitle";
    private static final String FEED_IDENT_FIELD_NAME = "feedIdent";
    private static final String IMPORTER_DESC_FIELD_NAME = "importerDesc";
    private static final String SOURCE_NAME_FIELD_NAME = "sourceName";
    private static final String SOURCE_URL_FIELD_NAME = "sourceUrl";
    private static final String IMPORTER_ID_FIELD_NAME = "importerId";
    private static final String POST_DESC_FIELD_NAME = "postDesc";
    private static final String POST_IMG_URL_FIELD_NAME = "postImgUrl";
    private static final String POST_URL_FIELD_NAME = "postUrl";
    private static final String POST_COMMENT_FIELD_NAME = "postComment";
    private static final String POST_RIGHTS_FIELD_NAME = "postRights";
    private static final String XML_BASE_FIELD_NAME = "xmlBase";
    private static final String CONTRIBUTOR_NAME_FIELD_NAME = "contributorName";
    private static final String CONTRIBUTOR_EMAIL_FIELD_NAME = "contributorEmail";
    private static final String AUTHOR_NAME_FIELD_NAME = "authorName";
    private static final String AUTHOR_EMAIL_FIELD_NAME = "authorEmail";
    private static final String POST_CATEGORY_FIELD_NAME = "postCategory";
    private static final String ENCLOSURE_URL_FIELD_NAME = "enclosureUrl";
    private static final String PUBLISH_TIMESTAMP_FIELD_NAME = "publishTimestamp";
    private static final String EXPIRATION_TIMESTAMP_FIELD_NAME = "expirationTimestamp";
    private static final String LAST_UPDATED_TIMESTAMP_FIELD_NAME = "lastUpdatedTimestamp";

    JsonElement buildPostsArray(List<StagingPost> stagingPosts) {
        JsonArray arr = new JsonArray();
        for (StagingPost s : stagingPosts) {
            JsonObject sObj = new JsonObject();
            // importer Id
            sObj.addProperty(IMPORTER_ID_FIELD_NAME, s.getImporterId());
            // feed ident
            sObj.addProperty(FEED_IDENT_FIELD_NAME, s.getFeedIdent());
            // importer description
            sObj.addProperty(IMPORTER_DESC_FIELD_NAME, s.getImporterDesc());
            // source name
            sObj.addProperty(SOURCE_NAME_FIELD_NAME, s.getSourceName());
            // source url
            sObj.addProperty(SOURCE_URL_FIELD_NAME, s.getSourceName());
            // post title
            sObj.addProperty(POST_TITLE_FIELD_NAME, s.getPostTitle());
            // post description
            sObj.addProperty(POST_DESC_FIELD_NAME, s.getPostDesc());
            // post URL
            sObj.addProperty(POST_URL_FIELD_NAME, s.getPostUrl());
            // post thumbnail URL
            sObj.addProperty(POST_IMG_URL_FIELD_NAME, s.getPostImgUrl());
            // post comment
            sObj.addProperty(POST_COMMENT_FIELD_NAME, s.getPostComment());
            // post rights
            sObj.addProperty(POST_RIGHTS_FIELD_NAME, s.getPostRights());
            // xml base
            sObj.addProperty(XML_BASE_FIELD_NAME, s.getXmlBase());
            // contributor name
            sObj.addProperty(CONTRIBUTOR_NAME_FIELD_NAME, s.getContributorName());
            // contributor email
            sObj.addProperty(CONTRIBUTOR_EMAIL_FIELD_NAME, s.getContributorEmail());
            // author name
            sObj.addProperty(AUTHOR_NAME_FIELD_NAME, s.getAuthorName());
            // author email
            sObj.addProperty(AUTHOR_EMAIL_FIELD_NAME, s.getAuthorEmail());
            // post category
            sObj.addProperty(POST_CATEGORY_FIELD_NAME, s.getPostCategory());
            // enclosure URL
            sObj.addProperty(ENCLOSURE_URL_FIELD_NAME, s.getEnclosureUrl());
            // publish timestamp
            sObj.addProperty(PUBLISH_TIMESTAMP_FIELD_NAME, ofNullable(s.getPublishTimestamp()).map(DATE_FORMATTER::format).orElse(null));
            // expiration timestamp
            sObj.addProperty(EXPIRATION_TIMESTAMP_FIELD_NAME, ofNullable(s.getExpirationTimestamp()).map(DATE_FORMATTER::format).orElse(null));
            // last updated timestamp
            sObj.addProperty(LAST_UPDATED_TIMESTAMP_FIELD_NAME, ofNullable(s.getLastUpdatedTimestamp()).map(DATE_FORMATTER::format).orElse(null));
            arr.add(sObj);
        }

        return arr;
    }
}
