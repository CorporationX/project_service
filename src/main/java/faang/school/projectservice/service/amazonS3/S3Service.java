package faang.school.projectservice.service.amazonS3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 clientAmazonS3;
    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Transactional
    public String uploadPicture(String folder, ByteArrayOutputStream pic, ObjectMetadata picMetadata) {
        String key = folder + "/" + picMetadata.getUserMetadata().get("originalFileName") + "." + System.currentTimeMillis();

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, new ByteArrayInputStream(pic.toByteArray()), picMetadata);
        log.info("An image with the size {} has started uploading to the cloud", pic.size());
        clientAmazonS3.putObject(putObjectRequest);
        log.info("An image with the size {} has finished uploading to the cloud", pic.size());

        return key;
    }

    @Transactional
    public InputStream getPicture(String key) {
        log.info("Received picture key: {}", key);
        InputStream picture = clientAmazonS3.getObject(bucketName, key).getObjectContent();
        log.debug("Received picture from S3");

        return picture;
    }

    @Transactional
    public void deletePicture(String key) {
        log.info("Deleting the image with the key: {} has started", key);
        clientAmazonS3.deleteObject(bucketName, key);
        log.info("Deleting the image with the key: {} has finished", key);
    }
}
