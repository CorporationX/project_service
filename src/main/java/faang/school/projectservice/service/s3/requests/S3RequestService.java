package faang.school.projectservice.service.s3.requests;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import com.amazonaws.services.s3.model.GetObjectRequest;
import org.springframework.stereotype.Component;

import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import faang.school.projectservice.config.properties.S3Properties;
import faang.school.projectservice.dto.resource.FileResourceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3RequestService {
    private final S3Properties properties;
    
    public PutObjectRequest createPutRequest(FileResourceDto multipartFileResource) {
        return new PutObjectRequest(
            properties.getBucketName(),
            createKey(multipartFileResource),
            multipartFileResource.getResourceInputStream(),
            createObjectMetadata(multipartFileResource)
        );
    }

    public GetObjectRequest createGetRequest(String key){
        return new GetObjectRequest(properties.getBucketName(),key);
    }

    public DeleteObjectRequest createDeleteRequest(String key) {
        return new DeleteObjectRequest(properties.getBucketName(), key);
    }
    
    private ObjectMetadata createObjectMetadata(FileResourceDto multipartFileResource) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFileResource.getSize());
        objectMetadata.setContentType(multipartFileResource.getContentType());
        return objectMetadata;
    }
    
    private String createKey(FileResourceDto multipartFileResource) {
        return String.format(
            "%s/%s_%s_%s",
            multipartFileResource.getFolderName(),
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            UUID.randomUUID(),
            multipartFileResource.getFileName()
        );
    }
}
