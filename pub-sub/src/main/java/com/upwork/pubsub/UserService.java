package com.upwork.pubsub;

import io.awspring.cloud.sns.core.SnsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final SnsTemplate snsTemplate;
    private final String topicArn;

    public UserService(SnsTemplate snsTemplate, @Value("${com.upwork.sns.topic-arn}") String topicArn) {
        this.snsTemplate = snsTemplate;
        this.topicArn = topicArn;
    }

    public void create(User user) {
        // assume code to persist user record to DB
        snsTemplate.convertAndSend(topicArn, user);
        log.info("Successfully published message to topic {}", topicArn);
    }

}