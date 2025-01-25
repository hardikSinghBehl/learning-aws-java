package com.upwork.storage;

import io.awspring.cloud.s3.S3Template;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class StorageServiceIT {

    // ... as configured in init-s3-bucket.sh
    private static final String BUCKET_NAME = "upwork-bucket";

    @Autowired
    private StorageService storageService;

    @Autowired
    private S3Template s3Template;

    @Test
    void whenSaveCalled_thenFileSavedToBucket() throws IOException {
        var key = RandomString.make() + ".txt";
        var content = RandomString.make(100);
        var fileToUpload = createTextFile(key, content);

        storageService.save(fileToUpload);

        var objectExists = s3Template.objectExists(BUCKET_NAME, key);
        assertThat(objectExists).isTrue();
    }

    @Test
    void whenSaveCalledWithMetadata_thenFileAndMetadataSavedToBucket() throws IOException {
        var key = RandomString.make() + ".txt";
        var content = RandomString.make(100);
        var fileToUpload = createTextFile(key, content);

        var metadataKey = RandomString.make();
        var metadataValue = RandomString.make();

        var metadata = Map.of(metadataKey, metadataValue);

        storageService.save(fileToUpload, metadata);

        var retrievedFile = storageService.get(key);
        assertThat(retrievedFile)
            .isNotNull()
            .satisfies(file -> {
                assertThat(file.metadata().containsKey(metadataKey));
                assertThat(file.metadata().containsValue(metadataValue));
            });
    }

    @Test
    void whenGetCalled_thenFileFetchedFromBucket() throws IOException {
        var key = RandomString.make() + ".txt";
        var content = RandomString.make(100);
        var fileToUpload = createTextFile(key, content);
        storageService.save(fileToUpload);

        var retrievedFile = storageService.get(key);
        var retrievedContent = readFile(retrievedFile.getContentAsByteArray());
        assertThat(retrievedContent).isEqualTo(content);
    }

    @Test
    void whenDeleteCalled_thenFileDeletedFromBucket() throws IOException {
        var key = RandomString.make() + ".txt";
        var content = RandomString.make(100);
        var fileToUpload = createTextFile(key, content);
        storageService.save(fileToUpload);

        var objectExists = s3Template.objectExists(BUCKET_NAME, key);
        assertThat(objectExists).isTrue();

        storageService.delete(key);

        objectExists = s3Template.objectExists(BUCKET_NAME, key);
        assertThat(objectExists).isFalse();
    }

    @Test
    void whenVieablePresignedUrlGenerated_thenObjectFetchIsAllowed() throws IOException {
        var key = RandomString.make(10) + ".txt";
        var fileContent = RandomString.make(50);
        var fileToUpload = createTextFile(key, fileContent);
        storageService.save(fileToUpload);

        var presignedUrl = storageService.generateViewablePresignedUrl(key);

        var restClient = RestClient.builder().build();
        var responseBody = restClient
            .method(HttpMethod.GET)
            .uri(URI.create(presignedUrl.toExternalForm()))
            .retrieve()
            .body(byte[].class);

        var retrievedContent = new String(responseBody, StandardCharsets.UTF_8);
        assertThat(retrievedContent).isEqualTo(fileContent);
    }

    @Test
    void whenUploadablePresignedUrlGenerated_thenObjectSaveSucceeds() throws IOException {
        var key = RandomString.make(10) + ".txt";
        var fileContent = RandomString.make(50);
        var fileToUpload = createTextFile(key, fileContent);
        var md5Hash = calculateContentMD5(fileContent);
        var contentLength = fileContent.length();

        var presignedUrl = storageService.generateUploadablePresignedUrl(key, md5Hash, contentLength);

        final var restClient = RestClient.builder().build();
        final var response = restClient
            .method(HttpMethod.PUT)
            .uri(URI.create(presignedUrl.toExternalForm()))
            .body(fileToUpload.getBytes())
            .retrieve()
            .toBodilessEntity();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        var objectExists = s3Template.objectExists(BUCKET_NAME, key);
        assertThat(objectExists).isTrue();
    }

    private MultipartFile createTextFile(String fileName, String content) throws IOException {
        var fileContentBytes = content.getBytes();
        var inputStream = new ByteArrayInputStream(fileContentBytes);
        return new MockMultipartFile(fileName, fileName, "text/plain", inputStream);
    }

    private String readFile(byte[] bytes) {
        var inputStreamReader = new InputStreamReader(new ByteArrayInputStream(bytes));
        return new BufferedReader(inputStreamReader).lines().collect(Collectors.joining("\n"));
    }

    private String calculateContentMD5(String content) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
            byte[] mdBytes = md.digest(contentBytes);
            return Base64.getEncoder().encodeToString(mdBytes);
        } catch (Exception exception) {
            throw new RuntimeException("Failed to calculate Content-MD5", exception);
        }
    }

}