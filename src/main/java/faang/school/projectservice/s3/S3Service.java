package faang.school.projectservice.s3;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
@Component
public interface S3Service<T> {
    T uploadFile(MultipartFile multipartFile, String folder);

    void deleteFile(String key);

    InputStream downloadFile(String key);
}