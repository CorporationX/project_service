package faang.school.projectservice.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface S3Service {
    void uploadFile(MultipartFile file, String key);
    void deleteFile(String key);
    InputStream downloadFile(String key);
}
