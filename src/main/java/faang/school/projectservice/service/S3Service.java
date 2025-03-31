package faang.school.projectservice.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface S3Service {

    String uploadFile(MultipartFile file, String folder);

    InputStream downloadFile(String key);

    void deleteFile(String key);
}
