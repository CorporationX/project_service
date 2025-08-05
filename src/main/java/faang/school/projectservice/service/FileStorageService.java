package faang.school.projectservice.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    void uploadFile(MultipartFile file, String key);
}
