package com.jangdu.community.global.storage;

import com.jangdu.community.global.exception.BusinessException;
import com.jangdu.community.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3StorageService implements StorageService {

    private final S3Client s3Client;
    private final StorageProperties storageProperties;

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    @Override
    public String upload(MultipartFile file, String directory) {
        validateFile(file);

        String key = generateKey(directory, file.getOriginalFilename());

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(storageProperties.getBucket())
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            String url = "https://%s.s3.%s.amazonaws.com/%s".formatted(
                    storageProperties.getBucket(),
                    storageProperties.getRegion(),
                    key
            );

            log.info("File uploaded: key={}, size={}", key, file.getSize());
            return url;
        } catch (IOException e) {
            log.error("File upload failed: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    @Override
    public void delete(String fileUrl) {
        String key = extractKeyFromUrl(fileUrl);

        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(storageProperties.getBucket())
                .key(key)
                .build();

        s3Client.deleteObject(request);
        log.info("File deleted: key={}", key);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException(ErrorCode.FILE_EMPTY);
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(ErrorCode.FILE_SIZE_EXCEEDED);
        }
        if (!ALLOWED_IMAGE_TYPES.contains(file.getContentType())) {
            throw new BusinessException(ErrorCode.FILE_TYPE_NOT_ALLOWED);
        }
    }

    private String generateKey(String directory, String originalFilename) {
        String extension = extractExtension(originalFilename);
        return "%s/%s.%s".formatted(directory, UUID.randomUUID(), extension);
    }

    private String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "bin";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    private String extractKeyFromUrl(String url) {
        String prefix = "https://%s.s3.%s.amazonaws.com/".formatted(
                storageProperties.getBucket(),
                storageProperties.getRegion()
        );
        return url.replace(prefix, "");
    }
}
