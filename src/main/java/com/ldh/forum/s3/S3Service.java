package com.ldh.forum.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class S3Service {

    private final S3Uploader s3Uploader;

    @Value("${cloud.aws.s3.bucket}")
    private String baseUrl;

    public S3Service(S3Uploader s3Uploader) {
        this.s3Uploader = s3Uploader;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        // After creating temp file, upload
        File tempFile = File.createTempFile("upload-", filename);
        file.transferTo(tempFile);

        // upload file with s3Uploader
        String fileUrl = s3Uploader.uploadFile(tempFile, filename);
        return fileUrl;
    }
}
