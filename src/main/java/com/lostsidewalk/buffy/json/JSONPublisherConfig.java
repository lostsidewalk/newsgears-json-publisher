package com.lostsidewalk.buffy.json;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JSONPublisherConfig {

    @Autowired
    JSONPublisherConfigProps configProps;

    @Bean
    FeedObjectBuilder feedObjectBuilder() {
        return new FeedObjectBuilder(configProps);
    }

    @Bean
    PostsArrayBuilder postsArrayBuilder() {
        return new PostsArrayBuilder();
    }
}
