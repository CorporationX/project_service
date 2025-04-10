package faang.school.projectservice.service;

import faang.school.projectservice.exception.FileStorageException;
import faang.school.projectservice.model.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static faang.school.projectservice.constants.Constants.UPLOAD_FAIL;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileStorageServiceImpl implements FileStorageService {
    private final ResourceService resourceService;

    @Override
    public Resource uploadFile(MultipartFile file, Long projectId, Long uploaderId) {

        try {
            log.info("Uploading file for project {} by user {}", projectId, uploaderId);
            return resourceService.uploadFile(file, uploaderId, projectId);
        } catch (Exception e) {
            throw new FileStorageException(UPLOAD_FAIL);
        }

    }

    @Override
    public void deleteFile(Long resourceId, Long currentMemberId) {
        log.info("Deleting file with ID {} by user", resourceId, currentMemberId);
        resourceService.deleteFile(resourceId, currentMemberId);
    }
}
