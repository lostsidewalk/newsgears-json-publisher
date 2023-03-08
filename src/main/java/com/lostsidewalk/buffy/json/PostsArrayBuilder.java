package com.lostsidewalk.buffy.json;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.lostsidewalk.buffy.post.*;
import org.apache.commons.lang3.time.FastDateFormat;

import java.lang.reflect.Type;
import java.util.List;

import static java.util.Optional.ofNullable;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.time.FastDateFormat.MEDIUM;

class PostsArrayBuilder {

    private static final Gson GSON = new Gson();

    private static final FastDateFormat DATE_FORMATTER = FastDateFormat.getDateTimeInstance(MEDIUM, MEDIUM);
    //
    //
    //
    private static final String POST_TITLE_FIELD_NAME = "postTitle";
    private static final String FEED_ID_FIELD_NAME = "feedId";
    private static final String IMPORTER_DESC_FIELD_NAME = "importerDesc";
    private static final String IMPORTER_ID_FIELD_NAME = "importerId";
    private static final String POST_DESC_FIELD_NAME = "postDesc";
    private static final String POST_CONTENTS_FIELD_NAME = "postContents";
    private static final String POST_MEDIA_FIELD_NAME = "postMedia";
    private static final String POST_ITUNES_FIELD_NAME = "postITunes";
    private static final String POST_IMG_URL_FIELD_NAME = "postImgUrl";
    private static final String POST_URL_FIELD_NAME = "postUrl";
    private static final String POST_URLS_FIELD_NAME = "postUrls";
    private static final String POST_COMMENT_FIELD_NAME = "postComment";
    private static final String POST_RIGHTS_FIELD_NAME = "postRights";
    private static final String CONTRIBUTORS_FIELD_NAME = "contributors";
    private static final String AUTHORS_FIELD_NAME = "authors";
    private static final String POST_CATEGORIES_FIELD_NAME = "postCategories";
    private static final String ENCLOSURES_FIELD_NAME = "enclosures";
    private static final String PUBLISH_TIMESTAMP_FIELD_NAME = "publishTimestamp";
    private static final String EXPIRATION_TIMESTAMP_FIELD_NAME = "expirationTimestamp";
    private static final String LAST_UPDATED_TIMESTAMP_FIELD_NAME = "lastUpdatedTimestamp";

    private static final Type LIST_OF_POST_PERSON_TYPE = new TypeToken<List<PostPerson>>() {}.getType();

    private static final Type LIST_OF_CONTENT_OBJECT_TYPE = new TypeToken<List<ContentObject>>() {}.getType();

    private static final Type LIST_OF_STRING_TYPE = new TypeToken<List<String>>() {}.getType();

    private static final Type LIST_OF_POST_URL_TYPE = new TypeToken<List<PostUrl>>() {}.getType();

    JsonElement buildPostsArray(List<StagingPost> stagingPosts) {
        JsonArray arr = new JsonArray();
        for (StagingPost s : stagingPosts) {
            JsonObject sObj = new JsonObject();
            // importer Id
            sObj.addProperty(IMPORTER_ID_FIELD_NAME, s.getImporterId());
            // feed ident
            sObj.addProperty(FEED_ID_FIELD_NAME, s.getFeedId());
            // importer description
            sObj.addProperty(IMPORTER_DESC_FIELD_NAME, s.getImporterDesc());
            // post title
            ContentObject postTitle = s.getPostTitle();
            if (postTitle != null) {
                JsonElement postTitleObj = GSON.toJsonTree(postTitle, ContentObject.class);
                sObj.add(POST_TITLE_FIELD_NAME, postTitleObj);
            }
            // post description
            ContentObject postDesc = s.getPostDesc();
            if (postDesc != null) {
                JsonElement postDescObj = GSON.toJsonTree(postDesc, ContentObject.class);
                sObj.add(POST_DESC_FIELD_NAME, postDescObj);
            }
            // post contents
            List<ContentObject> postContents = s.getPostContents();
            if (isNotEmpty(postContents)) {
                JsonElement contentsArr = GSON.toJsonTree(postContents, LIST_OF_CONTENT_OBJECT_TYPE);
                sObj.add(POST_CONTENTS_FIELD_NAME, contentsArr);
            }
            // post media
            PostMedia postMedia = s.getPostMedia();
            if (postMedia != null) {
                JsonElement postMediaObj = GSON.toJsonTree(postMedia, PostMedia.class);
                sObj.add(POST_MEDIA_FIELD_NAME, postMediaObj);
            }
            // post itunes
            PostITunes postITunes = s.getPostITunes();
            if (postITunes != null) {
                JsonElement postITunesObj = GSON.toJsonTree(postITunes, PostITunes.class);
                sObj.add(POST_ITUNES_FIELD_NAME, postITunesObj);
            }

            // post URL
            sObj.addProperty(POST_URL_FIELD_NAME, s.getPostUrl());
            // post URLs
            List<PostUrl> postUrls = s.getPostUrls();
            if (isNotEmpty(postUrls)) {
                JsonElement urlsArr = GSON.toJsonTree(postUrls, LIST_OF_POST_URL_TYPE);
                sObj.add(POST_URLS_FIELD_NAME, urlsArr);
            }
            // post thumbnail URL
            sObj.addProperty(POST_IMG_URL_FIELD_NAME, s.getPostImgUrl());
            // post comment
            sObj.addProperty(POST_COMMENT_FIELD_NAME, s.getPostComment());
            // post rights
            sObj.addProperty(POST_RIGHTS_FIELD_NAME, s.getPostRights());
            // contributors
            List<PostPerson> contributors = s.getContributors();
            if (isNotEmpty(contributors)) {
                JsonElement contributorsArr = GSON.toJsonTree(contributors, LIST_OF_POST_PERSON_TYPE);
                sObj.add(CONTRIBUTORS_FIELD_NAME, contributorsArr);
            }
            // authors 
            List<PostPerson> authors = s.getAuthors();
            if (isNotEmpty(authors)) {
                JsonElement authorsArr = GSON.toJsonTree(authors, LIST_OF_POST_PERSON_TYPE);
                sObj.add(AUTHORS_FIELD_NAME, authorsArr);
            }
            // post categories
            List<String> postCategories = s.getPostCategories();
            if (isNotEmpty(postCategories)) {
                JsonElement postCategoriesArr = GSON.toJsonTree(postCategories, LIST_OF_STRING_TYPE);
                sObj.add(POST_CATEGORIES_FIELD_NAME, postCategoriesArr);
            }
            // publish timestamp
            sObj.addProperty(PUBLISH_TIMESTAMP_FIELD_NAME, ofNullable(s.getPublishTimestamp()).map(DATE_FORMATTER::format).orElse(null));
            // expiration timestamp
            sObj.addProperty(EXPIRATION_TIMESTAMP_FIELD_NAME, ofNullable(s.getExpirationTimestamp()).map(DATE_FORMATTER::format).orElse(null));
            // enclosures
            List<PostEnclosure> enclosures = s.getEnclosures();
            if (isNotEmpty(enclosures)) {
                JsonElement enclosuresArr = GSON.toJsonTree(enclosures, JsonArray.class);
                sObj.add(ENCLOSURES_FIELD_NAME, enclosuresArr);
            }
            // last updated timestamp
            sObj.addProperty(LAST_UPDATED_TIMESTAMP_FIELD_NAME, ofNullable(s.getLastUpdatedTimestamp()).map(DATE_FORMATTER::format).orElse(null));
            arr.add(sObj);
        }

        return arr;
    }
}
