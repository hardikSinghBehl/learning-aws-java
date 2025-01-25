package com.upwork.storage;

import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
        s3Template.upload(bucketName, file.getOriginalFilename(), file.getInputStream());
    }

    public S3Resource get(String key) {
        return s3Template.download(bucketName, key);
    }

    public void delete(String key) {
        s3Template.deleteObject(bucketName, key);
    }

}