package faang.school.projectservice.service;

import faang.school.projectservice.exception.CoverMaxSizeException;
import faang.school.projectservice.exception.ExceptionMessage;
import faang.school.projectservice.exception.FileLimitException;
import faang.school.projectservice.exception.ProjectNotFoundException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectGallery;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectGalleryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class GalleryService {

    private final ProjectGalleryRepository projectGalleryRepository;
    private final MinioService minioService;
    private final ProjectService projectService;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final int MAX_FILE_COUNT = 50;

    @Transactional
    public String uploadImage(Long projectId, MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new CoverMaxSizeException(ExceptionMessage.FILE_IS_LARGE, toMegabytes(MAX_FILE_SIZE));
        }
        if (projectGalleryRepository.countByProjectId(projectId) >= MAX_FILE_COUNT) {
            throw new FileLimitException(ExceptionMessage.FILE_COUNT_LIMIT_EXCEEDED, MAX_FILE_COUNT);
        }
        String fileKey = file.getOriginalFilename() + "_" + UUID.randomUUID();
        minioService.uploadFile(file, fileKey);

        ProjectGallery savedFile = ProjectGallery.builder()
                .project(projectService.findById(projectId)
                        .orElseThrow(() -> new ProjectNotFoundException(ExceptionMessage.PROJECT_NOT_FOUND, projectId)))
                .fileKey(fileKey)
                .createdAt(LocalDateTime.now())
                .build();
        log.info("Saved file: {} \n With key : {}", file, fileKey);
        return projectGalleryRepository.save(savedFile).getFileKey();
    }

    public void deleteImage(Long projectId, String fileKey) {
        ProjectGallery entity = projectGalleryRepository.findByProjectId(projectId).stream()
                .filter(g -> g.getFileKey().equals(fileKey))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("File with name: " + fileKey + " not found"));

        minioService.deleteFile(fileKey);
        log.info("file with key : {} was deleted from minio", fileKey);
        projectGalleryRepository.delete(entity);
        log.info("file with key : {} was deleted from database", fileKey);
    }

    public List<String> getKeyListByProjectId(Long projectId) {
        Project project = projectService.findById(projectId).orElseThrow(() ->
                new ProjectNotFoundException(ExceptionMessage.PROJECT_NOT_FOUND, projectId));
        if (project.getVisibility() != ProjectVisibility.PUBLIC) {
            log.warn("Project visibility is not public");
        }
        return projectGalleryRepository.findByProjectId(projectId).stream()
                .map(ProjectGallery::getFileKey)
                .collect(toList());
    }

    private long toMegabytes(long bytes) {
        return bytes / (1024 * 1024);
    }
}
