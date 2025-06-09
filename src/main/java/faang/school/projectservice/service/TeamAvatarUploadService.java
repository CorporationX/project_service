package faang.school.projectservice.service;

import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

public interface TeamAvatarUploadService {
    public void uploadFile(MultipartFile file);
    public InputStream downloadFile();
    public void deleteFile(String key);
}
