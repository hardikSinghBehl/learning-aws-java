package com.upwork.storage;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    @Bean
    LocalStackContainer localStackContainer() {
        return new LocalStackContainer(DockerImageName.parse("localstack/localstack:4.0.3"))
            .withCopyFileToContainer(
                MountableFile.forClasspathResource("init-s3-bucket.sh", 0744),
                "/etc/localstack/init/ready.d/init-s3-bucket.sh"
            )
            .withServices(LocalStackContainer.Service.S3)
            .waitingFor(Wait.forLogMessage(".*Executed init-s3-bucket.sh.*", 1));
    }

    @Bean
    DynamicPropertyRegistrar dynamicPropertyRegistrar(LocalStackContainer localStackContainer) {
        return registry -> {
            registry.add("spring.cloud.aws.credentials.access-key", localStackContainer::getAccessKey);
            registry.add("spring.cloud.aws.credentials.secret-key", localStackContainer::getSecretKey);
            registry.add("spring.cloud.aws.s3.region", localStackContainer::getRegion);
            registry.add("spring.cloud.aws.s3.endpoint", localStackContainer::getEndpoint);

            // ... as configured in init-s3-bucket.sh
            registry.add("com.upwork.storage.s3.bucket-name", () -> "upwork-bucket");
        };
    }

}