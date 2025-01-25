package com.upwork.storage;

import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class StorageService {

    private static final Logger log = LoggerFactory.getLogger(StorageService.class);

    private final String bucketName;
    private final S3Template s3Template;

    public StorageService(@Value("${com.upwork.storage.s3.bucket-name}") String bucketName, S3Template s3Template) {
        this.bucketName = bucketName;
        this.s3Template = s3Template;
    }

    public void save(MultipartFile file) throws IOException {
        save(file, null);
    }

    public void save(MultipartFile file, @Nullable Map<String, String> metadata) throws IOException {
        var builder = ObjectMetadata.builder();
        if (metadata != null) {
            metadata.forEach((key, value) -> builder.metadata(key, value));
        }
        var objectMetadata = builder.build();
        s3Template.upload(bucketName, file.getOriginalFilename(), file.getInputStream(), objectMetadata);
        log.info("File '{}' uploaded to S3 bucket '{}'", file.getOriginalFilename(), bucketName);
    }

    public S3Resource get(String key) {
        var s3Resource = s3Template.download(bucketName, key);
        log.info("File '{}' downloaded from S3 bucket '{}'", key, bucketName);
        return s3Resource;
    }

    public void delete(String key) {
        s3Template.deleteObject(bucketName, key);
        log.info("File '{}' deleted from S3 bucket '{}'", key, bucketName);
    }

}