package com.lostsidewalk.buffy.json;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "json.publisher")
class JSONPublisherConfigProps {

    String channelLinkTemplate;

    String channelImageUrlTemplate;

    //
    //
    //

    public String getChannelLinkTemplate() {
        return channelLinkTemplate;
    }

    @SuppressWarnings("unused")
    public void setChannelLinkTemplate(String channelLinkTemplate) {
        this.channelLinkTemplate = channelLinkTemplate;
    }

    public String getChannelImageUrlTemplate() {
        return channelImageUrlTemplate;
    }

    @SuppressWarnings("unused")
    public void setChannelImageUrlTemplate(String channelImageUrlTemplate) {
        this.channelImageUrlTemplate = channelImageUrlTemplate;
    }
}
