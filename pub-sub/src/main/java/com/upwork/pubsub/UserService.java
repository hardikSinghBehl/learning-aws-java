package com.upwork.pubsub;

import io.awspring.cloud.sns.core.SnsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@EnableConfigurationProperties(AwsSnsConfigurationProperties.class)
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final SnsTemplate snsTemplate;
    private final AwsSnsConfigurationProperties awsSnsConfigurationProperties;

    public UserService(SnsTemplate snsTemplate, AwsSnsConfigurationProperties awsSnsConfigurationProperties) {
        this.snsTemplate = snsTemplate;
        this.awsSnsConfigurationProperties = awsSnsConfigurationProperties;
    }

    public void create(User user) {
        // assume code to persist user record to DB
        var topicArn = awsSnsConfigurationProperties.getTopicArn();
        snsTemplate.convertAndSend(topicArn, user);
        log.info("Successfully published message to topic {}", topicArn);
    }

}