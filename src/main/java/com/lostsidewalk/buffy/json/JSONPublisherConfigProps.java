package com.lostsidewalk.buffy.json;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
@ConfigurationProperties(prefix = "json.publisher")
class JSONPublisherConfigProps {

    String channelLinkTemplate;

    String channelImageUrlTemplate;

    //
    //
    //

    public final String getChannelLinkTemplate() {
        return channelLinkTemplate;
    }

    @SuppressWarnings("unused")
    public final void setChannelLinkTemplate(String channelLinkTemplate) {
        this.channelLinkTemplate = channelLinkTemplate;
    }

    public final String getChannelImageUrlTemplate() {
        return channelImageUrlTemplate;
    }

    @SuppressWarnings("unused")
    public final void setChannelImageUrlTemplate(String channelImageUrlTemplate) {
        this.channelImageUrlTemplate = channelImageUrlTemplate;
    }

    @Override
    public final String toString() {
        return "JSONPublisherConfigProps{" +
                "channelLinkTemplate='" + channelLinkTemplate + '\'' +
                ", channelImageUrlTemplate='" + channelImageUrlTemplate + '\'' +
                '}';
    }
}
