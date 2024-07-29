package faang.school.projectservice.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.projectservice.exception.FileException;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "services.s3.isMocked", havingValue = "false")
@Slf4j
public class S3Service {
    private final AmazonS3 s3Client;
    @Value("${services.s3.bucketName")
    private String bucketName;
    public Resource upload(MultipartFile file, String folder) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        String key = String.format("%s/%d%s", folder,System.currentTimeMillis(), file.getOriginalFilename());
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream(), metadata);
            s3Client.putObject(putObjectRequest);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new FileException("Error while uploading file");
        }
        Resource resource = new Resource();
        resource.setKey(key);
        resource.setName(file.getOriginalFilename());
        resource.setType(ResourceType.getResourceType(file.getContentType()));
        resource.setSize(BigInteger.valueOf(file.getSize()));
        return resource;
    }

    public void delete(String key) {
        try{
            s3Client.deleteObject(new DeleteObjectRequest(bucketName, key));
        }catch (Exception e){
            log.error(e.getMessage());
            throw new FileException("Error while deleting file");
        }
    }

}
