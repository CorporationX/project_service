package faang.school.projectservice.service.s3;

import faang.school.projectservice.dto.resource.ResourceObjectResponse;
import lombok.NonNull;

public interface S3Service {
    void uploadFile(byte[] fileContent, @NonNull String contentType, @NonNull String fileKey);
    void deleteFile(@NonNull String fileKey);
    ResourceObjectResponse downloadFile(@NonNull String fileKey);
}