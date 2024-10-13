package faang.school.projectservice.service.s3;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.config.properties.S3ConfigurationProperties;
import faang.school.projectservice.exception.S3Exception;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.service.image.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service implements FileStorageService {

    private final AmazonS3 amazonS3Client;
    private final S3ConfigurationProperties config;
    private final ImageService imageService;

    public void saveObject(MultipartFile file, Resource resource) {
        try {
            byte[] resizedImage = imageService.resizeImage(file);
            String key = resource.getProject().getName() + "/" + resource.getKey();

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(resizedImage.length);
            objectMetadata.setContentType(file.getContentType());

            PutObjectRequest request = new PutObjectRequest(
                    config.getBucketName(),
                    key,
                    new ByteArrayInputStream(resizedImage),
                    objectMetadata
            );
            amazonS3Client.putObject(request);
            log.debug("File uploaded successfully: {}", file.getOriginalFilename());
        } catch (SdkClientException e) {
            log.error("Client error occurred while uploading file '{}'. Error: {}", file.getOriginalFilename(), e.getMessage());
            throw new S3Exception("Couldn't upload file", e);
        }
    }

    public void deleteObject(String key, String projectName) {
        try {
            DeleteObjectRequest request = new DeleteObjectRequest(config.getBucketName(),
                    projectName + "/" + key);
            amazonS3Client.deleteObject(request);
            log.debug("Successfully deleted file with key {}", key);
        } catch (SdkClientException e) {
            log.error("{} occurred ,{}", e.getClass().getName(), e.getMessage());
            throw new S3Exception("Couldn't delete file", e);
        }
    }

    public S3Object getObject(Resource resource) {
        try {
            GetObjectRequest request = new GetObjectRequest(config.getBucketName(),
                    resource.getProject().getName() + "/" + resource.getKey());
            return amazonS3Client.getObject(request);
        } catch (SdkClientException e) {
            log.error("{} occurred ,{}", e.getClass().getName(), e.getMessage());
            throw new S3Exception("Couldn't get file", e);
        }
    }
}
