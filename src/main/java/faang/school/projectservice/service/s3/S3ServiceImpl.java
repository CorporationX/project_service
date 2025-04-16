package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.projectservice.excepion.FileException;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.dto.FileDto;
import faang.school.projectservice.properties.AmazonS3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {
    private final AmazonS3 amazonS3;
    private final AmazonS3Properties amazonS3Properties;

    @Override
    public Resource uploadFile(FileDto file) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        try (InputStream inputStream = file.getInputStream()) {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    amazonS3Properties.getBucketName(), file.getKey(), inputStream, objectMetadata);
            amazonS3.putObject(putObjectRequest);
        } catch (Exception e) {
            throw new FileException("connection error", e);
        }

        Resource resource = new Resource();
        resource.setKey(file.getKey());
        resource.setSize(BigInteger.valueOf(file.getSize()));
        resource.setStatus(ResourceStatus.ACTIVE);
        resource.setType(ResourceType.getResourceType(file.getContentType()));
        resource.setName(file.getOriginalFilename());
        return resource;
    }
}
