package faang.school.projectservice.service.s3.requests.project;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;

import faang.school.projectservice.config.properties.S3Properties;
import faang.school.projectservice.dto.resource.MultipartFileResourceDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.s3.requests.S3Request;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3ProjectCoverRequest implements S3Request {
    private final S3Properties properties;
    
    @Override
    public PutObjectRequest putRequest(MultipartFileResourceDto multipartFileResource) {
        return new PutObjectRequest(
            properties.getBucketName(),
            createKey(multipartFileResource),
            multipartFileResource.getResourceInputStream(),
            createObjectMetadata(multipartFileResource)
        );
    }
    
    @Override
    public String createKey(MultipartFileResourceDto multipartFileResource) {
        return String.format(
            "%s/%s_%s_%s",
            multipartFileResource.getFolderName(),
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            UUID.randomUUID(),
            multipartFileResource.getFileName()
        );
    }
    
    @Override
    public DeleteObjectRequest deleteRequest(String key) {
        return new DeleteObjectRequest(properties.getBucketName(), key);
    }
}
