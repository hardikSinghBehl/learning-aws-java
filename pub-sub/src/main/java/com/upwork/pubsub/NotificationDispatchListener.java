package com.upwork.pubsub;

import io.awspring.cloud.sqs.annotation.SnsNotificationMessage;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@RefreshScope
public class NotificationDispatchListener {

    private static final Logger logger = LoggerFactory.getLogger(NotificationDispatchListener.class);

    @SqsListener("${com.upwork.sqs.queue-url}")
    public void sendNotification(@SnsNotificationMessage User user) {
        logger.info("Dispatching email notification to user with name '{}' and id '{}'", user.getName(), user.getId());
    }

}