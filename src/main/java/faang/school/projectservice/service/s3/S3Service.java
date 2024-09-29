package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.model.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    @Value("${services.s3.bucketname}")
    private String bucketName;

    private final AmazonS3 amazonS3;

    public void saveObject(MultipartFile file, Resource resource) {
        try {
            PutObjectRequest request =
                    new PutObjectRequest(bucketName,
                            resource.getKey() + "/" + resource.getProject().getId(), file.getInputStream(),
                            amazonS3.getObjectMetadata(bucketName, resource.getKey()));
            amazonS3.putObject(request);
            log.debug("Successfully uploaded file! {}", file.getName());
        } catch (IOException e) {
            log.error("Something's gone wrong while uploading file! {}", (Object) e.getStackTrace());
        }
    }

    public void deleteObject(Resource resource) {
        DeleteObjectRequest request = new DeleteObjectRequest(bucketName,
                resource.getKey() + "/" + resource.getProject().getId());
        amazonS3.deleteObject(request);
        log.debug("Successfully deleted file with id");
    }

    public S3Object getObject(Resource resource) {
        return amazonS3.getObject(new GetObjectRequest(bucketName,
                resource.getKey() + "/" + resource.getProject().getId()));
    }
}
