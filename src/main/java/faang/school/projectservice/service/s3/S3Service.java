package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface S3Service {

    void putFileInStore(String key, InputStream stream, ObjectMetadata metadata);

    void putFileInStore(String key, InputStream stream);

    void uploadFile(MultipartFile file, String key);

    void deleteFile(String key);

    InputStream downloadFile(String key);
}
