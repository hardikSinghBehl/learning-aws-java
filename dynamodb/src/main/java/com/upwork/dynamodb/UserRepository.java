package com.upwork.dynamodb;

import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepository {

    private final DynamoDbTemplate dynamoDbTemplate;

    public UserRepository(DynamoDbTemplate dynamoDbTemplate) {
        this.dynamoDbTemplate = dynamoDbTemplate;
    }

    public void save(User user) {
        dynamoDbTemplate.save(user);
    }

    public void update(User user) {
        dynamoDbTemplate.update(user);
    }

    public void deleteById(UUID id) {
        var user = findById(id);
        dynamoDbTemplate.delete(user);
    }

    public User findById(UUID id) {
        var partitionKey = Key.builder().partitionValue(id.toString()).build();
        return Optional
            .ofNullable(dynamoDbTemplate.load(partitionKey, User.class))
            .orElseThrow(InvalidUserIdException::new);
    }

    public List<User> findAll() {
        return dynamoDbTemplate
            .scanAll(User.class)
            .items()
            .stream()
            .toList();
    }

    public User findByEmail(String email) {
        var expression = Expression.builder()
            .expression("#email = :email")
            .putExpressionName("#email", "email")
            .putExpressionValue(":email", AttributeValue.builder().s(email).build())
            .build();
        var scanRequest = ScanEnhancedRequest.builder().filterExpression(expression).build();
        return dynamoDbTemplate.scan(scanRequest, User.class)
            .items()
            .stream()
            .findFirst()
            .orElseThrow(InvalidUserEmailException::new);
    }

}