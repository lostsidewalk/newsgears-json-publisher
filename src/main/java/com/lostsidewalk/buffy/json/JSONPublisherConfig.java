package com.lostsidewalk.buffy.json;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The JSONPublisherConfig class is a Spring configuration class responsible for defining and configuring beans related to JSON feed publishing.
 * It uses Spring's annotation-based configuration to define and wire the necessary components used by the JSON publisher.
 */
@Slf4j
@Configuration
public class JSONPublisherConfig {

    @Autowired
    JSONPublisherConfigProps configProps;

    /**
     * Default constructor; initializes the object.
     */
    JSONPublisherConfig() {
    }

    /**
     * Defines a bean for the FeedObjectBuilder, which is responsible for building the JSON feed object.
     *
     * @return A new instance of FeedObjectBuilder configured with the provided JSONPublisherConfigProps.
     */
    @SuppressWarnings("DesignForExtension")
    @Bean
    FeedObjectBuilder feedObjectBuilder() {
        return new FeedObjectBuilder(configProps);
    }

    @Override
    public final String toString() {
        return "JSONPublisherConfig{" +
                "configProps=" + configProps +
                '}';
    }
}
