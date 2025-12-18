package com.example.booksearch.infrastructure.image.gcs;

import com.example.booksearch.infrastructure.image.ImageService;
import com.google.cloud.storage.Blob;
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
        String fileName = UUID.randomUUID().toString();
        String contentType = file.getContentType();

        String imageUrl = null;
        if (!file.isEmpty()) {
            BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, fileName)
                    .setContentType(contentType)
                    .build();
            storage.create(blobInfo, file.getBytes());

            imageUrl = String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);
        }

        return imageUrl;
    }
}
