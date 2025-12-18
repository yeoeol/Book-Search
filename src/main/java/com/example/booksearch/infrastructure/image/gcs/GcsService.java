package com.example.booksearch.infrastructure.image.gcs;

import com.example.booksearch.infrastructure.image.ImageService;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GcsService implements ImageService {

    private final Storage storage;

    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;

    @Override
    public String uploadImage(MultipartFile file) throws IOException {
        String imageUrl = null;
        if (!file.isEmpty()) {
            imageUrl = uploadImageBytes(file.getBytes(), file.getContentType());
        }
        return imageUrl;
    }

    public String uploadImageBytes(byte[] content, String contentType) {
        String fileName = UUID.randomUUID().toString();

        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, fileName)
                .setContentType(contentType)
                .build();

        storage.create(blobInfo, content);

        return String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);
    }
}
