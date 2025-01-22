package com.upwork.pubsub;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("com.upwork.sns")
public class AwsSnsConfigurationProperties {

    private String topicArn;

    public String getTopicArn() {
        return topicArn;
    }

    public void setTopicArn(String topicArn) {
        this.topicArn = topicArn;
    }

}