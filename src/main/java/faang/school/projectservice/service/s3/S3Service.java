package faang.school.projectservice.service.s3;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface S3Service {
    String uploadFile(MultipartFile file, String folder);
    void deleteFile(String key);
    InputStream downloadFile(String key);
    String uploadCover(MultipartFile file);
}
