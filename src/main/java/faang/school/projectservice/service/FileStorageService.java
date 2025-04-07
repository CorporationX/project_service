package faang.school.projectservice.service;

import faang.school.projectservice.model.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileStorageService {
    private final ResourceService resourceService;

    public Resource uploadFile(MultipartFile file, Long projectId, Long uploaderId) throws IOException {
        log.info("Uploading file for project {} by user {}", projectId, uploaderId);
        return resourceService.uploadFile(file, uploaderId, projectId);
    }

    public void deleteFile(Long resourseId, Long currentMemberId) {
        log.info("Deleting file with ID {} by user", resourseId, currentMemberId);
        resourceService.deleteFile(resourseId, currentMemberId);
    }
}
