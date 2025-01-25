package com.upwork.storage;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/storage")
public class StorageController {

    private final StorageService storageService;

    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Void> uploadFile(
        @RequestPart("file") MultipartFile file,
        @RequestPart(required = false) Map<String, String> metadata
    ) throws IOException {
        storageService.save(file, metadata);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/download/{key}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String key) {
        var resource = storageService.get(key);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + key + "\"")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(resource);
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<Void> deleteFile(@PathVariable String key) {
        storageService.delete(key);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/view-url/{key}")
    public ResponseEntity<URL> getViewUrl(@PathVariable String key) {
        URL url = storageService.generateViewablePresignedUrl(key);
        return ResponseEntity.ok(url);
    }

    @GetMapping("/upload-url")
    public ResponseEntity<URL> getUploadUrl(
        @RequestParam String key,
        @RequestParam String md5Hash,
        @RequestParam long contentLength
    ) {
        URL url = storageService.generateUploadablePresignedUrl(key, md5Hash, contentLength);
        return ResponseEntity.ok(url);
    }

}