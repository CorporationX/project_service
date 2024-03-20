package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    @Qualifier("s3Client")
    private final AmazonS3 s3Client;
    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Override
    public Resource uploadFile(byte[] bytes, String folderName, String originalFileName, ObjectMetadata metadata, Long projectId) {
        String key = String.format("%s/%s_%s", folderName, originalFileName, LocalDateTime.now());
        PutObjectRequest objectRequest = new PutObjectRequest(bucketName, key, new ByteArrayInputStream(bytes), metadata);
        s3Client.putObject(objectRequest);

        return Resource.builder()
                .key(key)
                .name(originalFileName)
                .size(BigInteger.valueOf(metadata.getContentLength()))
                .type(ResourceType.getResourceType(metadata.getContentType()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(ResourceStatus.ACTIVE)
                .project(Project.builder().id(projectId).build())
                .build();
    }

    @Override
    public void deleteFile(String key) {
        s3Client.deleteObject(new DeleteObjectRequest(bucketName, key));
    }

}
