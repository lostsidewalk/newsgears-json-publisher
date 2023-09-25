package com.lostsidewalk.buffy.json;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The JSONPublisherConfig class is a Spring configuration class responsible for defining and configuring beans related to JSON feed publishing.
 * It uses Spring's annotation-based configuration to define and wire the necessary components used by the JSON publisher.
 */
@Configuration
public class JSONPublisherConfig {

    @Autowired
    JSONPublisherConfigProps configProps;

    /**
     * Defines a bean for the FeedObjectBuilder, which is responsible for building the JSON feed object.
     *
     * @return A new instance of FeedObjectBuilder configured with the provided JSONPublisherConfigProps.
     */
    @Bean
    FeedObjectBuilder feedObjectBuilder() {
        return new FeedObjectBuilder(configProps);
    }

    /**
     * Defines a bean for the PostsArrayBuilder, which is responsible for building the array of posts in the JSON feed.
     *
     * @return A new instance of PostsArrayBuilder.
     */
    @Bean
    PostsArrayBuilder postsArrayBuilder() {
        return new PostsArrayBuilder();
    }
}
