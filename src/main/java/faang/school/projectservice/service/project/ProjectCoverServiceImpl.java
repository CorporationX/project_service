package faang.school.projectservice.service.project;

import com.amazonaws.services.s3.model.ObjectMetadata;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectCoverDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.mapper.ProjectCoverMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.s3.AmazonS3Service;
import faang.school.projectservice.util.project.ImageProcessor;
import faang.school.projectservice.validation.project.ProjectValidator;
import faang.school.projectservice.validation.team_member.TeamMemberValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectCoverServiceImpl implements ProjectCoverService {
    private final TeamMemberValidator teamMemberValidator;
    private final ProjectCoverMapper projectCoverMapper;
    private final ProjectRepository projectRepository;
    private final ProjectValidator projectValidator;
    private final AmazonS3Service amazonS3Service;
    private final ImageProcessor imageProcessor;
    private final UserContext userContext;


    @Override
    @Transactional
    public ProjectCoverDto save(long projectId, MultipartFile file) {
        validateCurrentUser(projectId);
        String coverImageId = uploadCover(projectId, file);

        Project project = getById(projectId);
        project.setCoverImageId(coverImageId);
        project.setUpdatedAt(LocalDateTime.now());
        project = projectRepository.save(project);

        return projectCoverMapper.toDto(project);
    }

    @Override
    @Transactional(readOnly = true)
    public InputStreamResource getByProjectId(long projectId) {
        long currentUserId = userContext.getUserId();
        teamMemberValidator.validateExistenceByUserIdAndProjectId(currentUserId, projectId);
        Project project = getById(projectId);

        return amazonS3Service.downloadFile(project.getCoverImageId());
    }

    @Override
    @Transactional
    public ProjectCoverDto changeProjectCover(long projectId, MultipartFile file) {
        validateCurrentUser(projectId);

        Project project = getById(projectId);
        deleteOldCoverImage(project);

        String coverImageId = uploadCover(projectId, file);
        project.setCoverImageId(coverImageId);
        project.setUpdatedAt(LocalDateTime.now());
        project = projectRepository.save(project);

        return projectCoverMapper.toDto(project);
    }

    @Override
    @Transactional
    public ProjectCoverDto deleteByProjectId(long projectId) {
        validateCurrentUser(projectId);
        Project project = getById(projectId);

        String imageCoverId = project.getCoverImageId();
        amazonS3Service.deleteFile(imageCoverId);

        project.setCoverImageId(null);
        project = projectRepository.save(project);

        return projectCoverMapper.toDto(project);
    }

    public Project getById(long projectId) {
        Optional<Project> optional = projectRepository.findById(projectId);
        return optional.orElseThrow(() -> {
            String message = String.format("a project with %d does not exist", projectId);

            return new DataValidationException(message);
        });
    }

    private void validateCurrentUser(long projectId) {
        long currentUserId = userContext.getUserId();
        projectValidator.validateProjectIdAndCurrentUserId(projectId, currentUserId);
    }

    private String uploadCover(long projectId, MultipartFile file) {
        Pair<InputStream, ObjectMetadata> processedFile = imageProcessor.processCover(file);
        String path = String.format("project/cover/%d", projectId);

        return amazonS3Service.uploadFile(path, processedFile);
    }

    private void deleteOldCoverImage(Project project) {
        String oldImageCoverId = project.getCoverImageId();

        if (StringUtils.hasText(oldImageCoverId)) {
            amazonS3Service.deleteFile(oldImageCoverId);
        }
    }
}
