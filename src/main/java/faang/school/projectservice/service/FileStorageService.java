package faang.school.projectservice.service;

import faang.school.projectservice.model.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    Resource uploadFile(MultipartFile file, Long projectId, Long uploaderId);

    void deleteFile(Long resourceId, Long currentMemberId);
}
