package faang.school.projectservice.s3;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Client s3;

    public boolean uploadFile(MultipartFile file, String bucketName, String path) {
        ObjectMetadata data = new ObjectMetadata();
        data.setContentType(file.getContentType());
        data.setContentLength(file.getSize());
        try (InputStream inputStream = file.getInputStream()) {
            s3.client.putObject(bucketName, path, inputStream, data);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return true;
    }

    public S3Object downloadFile(String bucketName, String path) {
        return s3.client.getObject(bucketName, path);
    }

    public void deleteFile(String bucketName, String path) {
        s3.client.deleteObject(bucketName, path);
    }

    public boolean doesObjectExist(String bucketName, String path) {
        return s3.client.doesObjectExist(bucketName, path);
    }
}
