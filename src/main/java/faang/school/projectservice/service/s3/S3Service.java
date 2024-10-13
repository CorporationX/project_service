package faang.school.projectservice.service.s3;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface S3Service {
    String upload(MultipartFile file, String folder);
    void delete(String key);
    InputStream download(String key);
}
