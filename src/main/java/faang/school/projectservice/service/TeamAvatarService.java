package faang.school.projectservice.service;

import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

public interface TeamAvatarService {
    void uploadFile(long teamId, MultipartFile file);
    InputStream downloadFile(long teamId);
    void deleteFile(long teamId);
    String getAvatarContentType(long teamId);
}
