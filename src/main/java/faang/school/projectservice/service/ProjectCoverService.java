package faang.school.projectservice.service;

import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.service.s3.S3Service;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RequiredArgsConstructor
@Service
@Slf4j
@RequestMapping("/projects/{projectId}/cover")
public class ProjectCoverService {

    private final ProjectRepository projectRepository;
    private final ResourceRepository resourceRepository;
    private final S3Service s3Service;
    @Value("${max.file.size}")
    private final int maxFileSize;



    @Transactional
    public void addCover(Long projectId, MultipartFile file) {
        Project project = projectRepository.findById(projectId).orElseThrow();
        String key = String.valueOf(projectId);
        if (file.getSize() > maxFileSize) {
            String message = String.format("File size should be less than %d MB", maxFileSize);
            log.error(message);
            throw new IllegalArgumentException(message);
        }

        s3Service.uploadFile(file, key);
        project.setCoverImageId(key);

        projectRepository.save(project);
        log.info("added cover image with key{} for projectId: {}", key, projectId);
    }

    public InputStream downloadCover(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow();
        String key = project.getCoverImageId();
        return s3Service.downloadFile(key);
    }

    public void updateCover(Long projectId, MultipartFile file, Long userId) {
        Project project = projectRepository.findById(projectId).orElseThrow();
        String key = project.getCoverImageId();
        isAllowed(project.getOwnerId(), userId);
        s3Service.uploadFile(file, key);
        log.info("updated cover image with key: {}", key);
    }

    public void deleteCover(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId).orElseThrow();
        String key = project.getCoverImageId();
        isAllowed(project.getOwnerId(), userId);
        s3Service.deleteFile(String.valueOf(key));
        log.info("deleted cover image with key: {}", key);
    }

    private void isAllowed(Long ownerId, Long userId) {
        if (!ownerId.equals(userId)) {
            String message = "You are not allowed to change this cover image";
            log.info(message);
            throw new ForbiddenException(message);
        }
    }
}