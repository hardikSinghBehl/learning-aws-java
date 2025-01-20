package com.upwork.pubsub;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

@Component
public class SecretFetcher {

    private final SecretsManagerClient secretsManagerClient;

    public SecretFetcher(SecretsManagerClient secretsManagerClient) {
        this.secretsManagerClient = secretsManagerClient;
    }

    public String fetch(String secretName) {
        var secretValues = secretsManagerClient.getSecretValue(request -> request.secretId(secretName));
        return secretValues.secretString();
    }

}