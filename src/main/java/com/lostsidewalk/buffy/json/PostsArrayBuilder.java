package com.lostsidewalk.buffy.json;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.lostsidewalk.buffy.post.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.FastDateFormat;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import static java.util.Optional.ofNullable;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.time.FastDateFormat.MEDIUM;


@Slf4j
class PostsArrayBuilder {

    private static final Gson GSON = new Gson();

    private static final FastDateFormat DATE_FORMATTER = FastDateFormat.getDateTimeInstance(MEDIUM, MEDIUM);
    //
    //
    //
    private static final String POST_TITLE_FIELD_NAME = "postTitle";
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

    @SuppressWarnings("EmptyClass")
    private static final Type LIST_OF_POST_PERSON_TYPE = new TypeToken<List<PostPerson>>() {}.getType();

    @SuppressWarnings("EmptyClass")
    private static final Type LIST_OF_CONTENT_OBJECT_TYPE = new TypeToken<List<ContentObject>>() {}.getType();

    @SuppressWarnings("EmptyClass")
    private static final Type LIST_OF_STRING_TYPE = new TypeToken<List<String>>() {}.getType();

    @SuppressWarnings("EmptyClass")
    private static final Type LIST_OF_POST_URL_TYPE = new TypeToken<List<PostUrl>>() {}.getType();

    @SuppressWarnings("EmptyClass")
    private static final Type LIST_OF_ENCLOSURE_TYPE = new TypeToken<List<PostEnclosure>>() {}.getType();

    static JsonElement buildPostsArray(Iterable<? extends StagingPost> stagingPosts) {
        JsonArray arr = new JsonArray();
        for (StagingPost stagingPost : stagingPosts) {
            JsonObject sObj = new JsonObject();
            // post title
            ContentObject postTitle = stagingPost.getPostTitle();
            if (postTitle != null) {
                sObj.addProperty(POST_TITLE_FIELD_NAME, postTitle.getValue());
            }
            // post description
            ContentObject postDesc = stagingPost.getPostDesc();
            if (postDesc != null) {
                sObj.addProperty(POST_DESC_FIELD_NAME, postDesc.getValue());
            }
            // post contents
            List<ContentObject> postContents = stagingPost.getPostContents();
            if (isNotEmpty(postContents)) {
                JsonElement contentsArr = GSON.toJsonTree(postContents, LIST_OF_CONTENT_OBJECT_TYPE);
                sObj.add(POST_CONTENTS_FIELD_NAME, contentsArr);
            }
            // post media
            PostMedia postMedia = stagingPost.getPostMedia();
            if (postMedia != null) {
                JsonElement postMediaObj = GSON.toJsonTree(postMedia, PostMedia.class);
                sObj.add(POST_MEDIA_FIELD_NAME, postMediaObj);
            }
            // post itunes
            PostITunes postITunes = stagingPost.getPostITunes();
            if (postITunes != null) {
                JsonElement postITunesObj = GSON.toJsonTree(postITunes, PostITunes.class);
                sObj.add(POST_ITUNES_FIELD_NAME, postITunesObj);
            }

            // post URL
            sObj.addProperty(POST_URL_FIELD_NAME, stagingPost.getPostUrl());
            // post URLs
            List<PostUrl> postUrls = stagingPost.getPostUrls();
            if (isNotEmpty(postUrls)) {
                JsonElement urlsArr = GSON.toJsonTree(postUrls, LIST_OF_POST_URL_TYPE);
                sObj.add(POST_URLS_FIELD_NAME, urlsArr);
            }
            // post thumbnail URL
            String postImgUrl = stagingPost.getPostImgUrl();
            if (isNotBlank(postImgUrl)) {
                sObj.addProperty(POST_IMG_URL_FIELD_NAME, postImgUrl);
            }
            // post comment
            String postComment = stagingPost.getPostComment();
            if (isNotBlank(postComment)) {
                sObj.addProperty(POST_COMMENT_FIELD_NAME, stagingPost.getPostComment());
            }

            // post rights
            String postRights = stagingPost.getPostRights();
            if (isNotBlank(postRights)) {
                sObj.addProperty(POST_RIGHTS_FIELD_NAME, postRights);
            }
            // contributors
            List<PostPerson> contributors = stagingPost.getContributors();
            if (isNotEmpty(contributors)) {
                JsonElement contributorsArr = GSON.toJsonTree(contributors, LIST_OF_POST_PERSON_TYPE);
                sObj.add(CONTRIBUTORS_FIELD_NAME, contributorsArr);
            }
            // authors 
            List<PostPerson> authors = stagingPost.getAuthors();
            if (isNotEmpty(authors)) {
                JsonElement authorsArr = GSON.toJsonTree(authors, LIST_OF_POST_PERSON_TYPE);
                sObj.add(AUTHORS_FIELD_NAME, authorsArr);
            }
            // post categories
            List<String> postCategories = stagingPost.getPostCategories();
            if (isNotEmpty(postCategories)) {
                JsonElement postCategoriesArr = GSON.toJsonTree(postCategories, LIST_OF_STRING_TYPE);
                sObj.add(POST_CATEGORIES_FIELD_NAME, postCategoriesArr);
            }
            // publish timestamp
            sObj.addProperty(PUBLISH_TIMESTAMP_FIELD_NAME, ofNullable(stagingPost.getPublishTimestamp()).map(DATE_FORMATTER::format).orElse(null));
            // expiration timestamp
            Date expirationTimestamp = stagingPost.getExpirationTimestamp();
            if (expirationTimestamp != null) {
                sObj.addProperty(EXPIRATION_TIMESTAMP_FIELD_NAME, DATE_FORMATTER.format(expirationTimestamp));
            }
            // enclosures
            List<PostEnclosure> enclosures = stagingPost.getEnclosures();
            if (isNotEmpty(enclosures)) {
                JsonElement enclosuresArr = GSON.toJsonTree(enclosures, LIST_OF_ENCLOSURE_TYPE);
                sObj.add(ENCLOSURES_FIELD_NAME, enclosuresArr);
            }
            // last updated timestamp
            Date lastUpdatedTimestamp = stagingPost.getLastUpdatedTimestamp();
            if (lastUpdatedTimestamp != null) {
                sObj.addProperty(LAST_UPDATED_TIMESTAMP_FIELD_NAME, DATE_FORMATTER.format(lastUpdatedTimestamp));
            }
            //
            arr.add(sObj);
        }

        return arr;
    }
}
