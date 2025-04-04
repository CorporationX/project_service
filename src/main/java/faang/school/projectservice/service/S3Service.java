package faang.school.projectservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${services.s3.bucketName}")
    private String bucketName;
    private final ImageCompressionService imageCompressionService;

    public Resource uploadFile(MultipartFile file, String folder) {
        byte[] compressedFileBytes = imageCompressionService.compressFile(file);

        String key = generateS3Key(folder, file.getOriginalFilename());
        ObjectMetadata metadata = buildMetadata(compressedFileBytes.length, file.getContentType());

        PutObjectRequest putObjectRequest = new PutObjectRequest(
                bucketName, key, new ByteArrayInputStream(compressedFileBytes), metadata);
        amazonS3.putObject(putObjectRequest);

        return buildResource(file,key,compressedFileBytes.length);
    }

    public void deleteFile(String key) {
        amazonS3.deleteObject(bucketName, key);
    }

    private String generateS3Key(String folder, String originalFilename) {
        return String.format("%s%d%s", folder, System.currentTimeMillis(), originalFilename);
    }

    private ObjectMetadata buildMetadata(long contentLength, String contentType) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(contentLength);
        metadata.setContentType(contentType);
        return metadata;
    }

    private Resource buildResource(MultipartFile file, String key, long size) {
        Resource resource = new Resource();
        resource.setName(file.getOriginalFilename());
        resource.setKey(key);
        resource.setSize(BigInteger.valueOf(size));
        resource.setAllowedRoles(List.of(TeamRole.MANAGER));
        resource.setType(ResourceType.IMAGE);
        resource.setStatus(ResourceStatus.ACTIVE);
        return resource;
    }
}
