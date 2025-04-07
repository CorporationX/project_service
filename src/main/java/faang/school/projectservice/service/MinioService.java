package faang.school.projectservice.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface MinioService {
    void uploadFile(String key, MultipartFile file) throws IOException;

    void deleteFile(String key);
}
