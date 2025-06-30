package faang.school.projectservice.service.CoverForProjectService;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

import static faang.school.projectservice.validation.CoverValidation.validateFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectCoverService {

    private final ProjectRepository projectRepository;
    private final S3Service s3Service;
    private final UserContext userContext;
    private final UserServiceClient userServiceClient;

    @Transactional
    public Project addImageToProject(Long projectId, MultipartFile file) {

        validateFile(file);

        UserDto currentUser = userServiceClient.getUser(userContext.getUserId());
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new DataValidationException("Project not found with id: " + projectId));

        if (!project.getOwnerId().equals(currentUser.id())) {
            throw new DataValidationException("Only project owner can add cover");
        }

        String key = String.format("%s - %s", System.currentTimeMillis(), file.getOriginalFilename());
        s3Service.uploadFile(key, file);

        if (project.getCoverImageId() != null) {
            s3Service.deleteFile(project.getCoverImageId());
        }

        project.setCoverImageId(key);
        project.setUpdatedAt(LocalDateTime.now());

        log.info("Added cover to project {} by user {}", projectId, currentUser.id());
        return projectRepository.save(project);
    }

    @Transactional
    public void removeCoverFromProjectById(Long projectId) {

        UserDto currentUser = userServiceClient.getUser(userContext.getUserId());
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new DataValidationException("Project not found with id: " + projectId));

        if (!project.getOwnerId().equals(currentUser.id())) {
            throw new DataValidationException("Only project owner can remove cover");
        }

        if (project.getCoverImageId() == null || project.getCoverImageId().isBlank()) {
            throw new DataValidationException("Project does not have a cover to remove");
        }

        try {
            s3Service.deleteFile(project.getCoverImageId());
        } catch (Exception e) {
            log.warn("Failed to delete cover file from S3 for project {}, error: {}", projectId, e.getMessage());
        }

        project.setCoverImageId(null);
        project.setUpdatedAt(LocalDateTime.now());
        projectRepository.save(project);

        log.info("Removed cover from project {} by user {}", projectId, currentUser.id());
    }
}