package faang.school.projectservice.service.projectresource;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.service.amazonS3Service.AmazonS3Service;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.teamMember.TeamMemberService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static faang.school.projectservice.model.ProjectVisibility.PRIVATE;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectResourceService {
    private final AmazonS3Service amazonS3Service;
    private final ProjectService projectService;
    private final TeamMemberService teamMemberService;
    private final ResourceRepository resourceRepository;
    private final ImageChecker imageChecker;
    private static final long MAX_FILE_SIZE_BYTES = 5 * 1024L * 1024L;

    @Transactional
    public Resource addFile(long projectResourceId, long userId, MultipartFile file) {
        Project project = projectService.getProject(projectResourceId);

        BigInteger fileSize = BigInteger.valueOf(file.getSize());
        BigInteger storageSize = project.getStorageSize().add(fileSize);

        checkStorageForEnoughMemory(storageSize, project.getMaxStorageSize());
        project.setStorageSize(storageSize);

        String key = String.format("%s/%d %s", projectResourceId + project.getName(),
                System.currentTimeMillis(),
                file.getOriginalFilename());

        TeamMember author = getTeamMember(userId, projectResourceId);

        Resource resource = amazonS3Service.addResource(file, key);
        setGalleryFileKeyIfFileIsImage(userId, file, project, key);
        resource.setProject(project);
        resource.setCreatedBy(author);
        resource.setUpdatedBy(author);

        resourceRepository.save(resource);
        projectService.saveProject(project);

        log.info("The file has been added " + resource.getName());
        return resource;
    }

    @Transactional
    public Resource updateFile(long projectResourceId, Long userId, MultipartFile file) {
        Resource resource = getResource(projectResourceId);
        Project project = resource.getProject();

        BigInteger fileSize = BigInteger.valueOf(file.getSize());
        BigInteger storageSize = project.getStorageSize().add(fileSize);
        storageSize = storageSize.subtract(resource.getSize());
        checkStorageForEnoughMemory(storageSize, project.getMaxStorageSize());

        project.setStorageSize(storageSize);

        TeamMember updatedBy = getTeamMember(userId, project.getId());

        resource.setUpdatedBy(updatedBy);
        resource.setUpdatedAt(LocalDateTime.now());
        resource.setSize(BigInteger.valueOf(file.getSize()));

        resourceRepository.save(resource);
        projectService.saveProject(project);

        amazonS3Service.updateResource(file, resource.getKey());

        log.info("The file has been changed " + resource.getName());
        return resource;
    }

    @Transactional
    public Resource removeFile(long projectResourceId, long userId) {
        Resource resource = getResource(projectResourceId);
        Project project = resource.getProject();

        TeamMember teamMember = getTeamMember(userId, project.getId());
        checkAccessForRemoval(userId, resource, teamMember.getRoles());

        resource.setStatus(ResourceStatus.DELETED);
        resource.setUpdatedAt(LocalDateTime.now());
        resource.setUpdatedBy(teamMember);

        project.setStorageSize(project.getStorageSize().subtract(resource.getSize()));
        resource.setSize(BigInteger.valueOf(0));

        amazonS3Service.completeRemoval(resource.getKey());
        resource.setKey(null);

        resourceRepository.save(resource);
        projectService.saveProject(project);

        log.info("The file has been deleted " + resource.getName());
        return resource;
    }

    public List<String> getAllProjectImages(long projectId) {
        checkProjectVisibility(projectId);
        Project project = projectService.getProject(projectId);
        return getImageUrlsList(project);
    }

    private void checkStorageForEnoughMemory(@NotNull BigInteger currentStorageSize,
                                             @NotNull BigInteger maxStorageSize) {
        if (currentStorageSize.compareTo(maxStorageSize) > 0) {
            throw new IllegalArgumentException("Exceeded the 2GB volume");
        }
    }

    private void checkAccessForRemoval(long userId, @NotNull Resource resource,
                                       @NotNull List<TeamRole> teamRoles) {
        if (userId != resource.getCreatedBy().getUserId() &&
                !teamRoles.contains(TeamRole.MANAGER)) {
            throw new IllegalArgumentException(
                    "Access error, only the author or manager can delete the file");
        }
    }

    private TeamMember getTeamMember(long userId, long projectId) {
        return teamMemberService.getTeamMemberByIdAndProjectId(userId, projectId);
    }

    @Transactional
    private Resource getResource(long resourceId) {
        return resourceRepository.findById(resourceId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Resource not found by id " + resourceId));
    }

    private void setGalleryFileKeyIfFileIsImage(long userId,
                                                MultipartFile file, Project project, String key) {
        if (imageChecker.checkResourceTypeIsImage(file.getContentType()) &&
                checkTeamMember(userId, project.getId())) {
            checkFileSizeNoMoreThanFiveMb(file);
            checkNotMoreThanFiftyImages(project.getId());
            project.getGalleryFileKeys().add(key);
        }
    }

    private boolean checkTeamMember(Long userId, Long projectResourceId) {
        return (getTeamMember(userId, projectResourceId).getClass() == TeamMember.class);
    }

    private void checkFileSizeNoMoreThanFiveMb(MultipartFile file) {
        long fileSizeInBytes = file.getSize();
        if (fileSizeInBytes > MAX_FILE_SIZE_BYTES) {
            throw new IllegalArgumentException("The image size should not exceed 5 MB");
        }
    }

    private void checkNotMoreThanFiftyImages(long projectResourceId) {
        Project project = projectService.getProject(projectResourceId);
        int numberOfImages = project.getGalleryFileKeys().size();
        if (numberOfImages >= 50) {
            throw new IllegalArgumentException("The number of images in the gallery can not exceed 50 pieces.");
        }
    }

    @Transactional
    private void checkProjectVisibility(long projectId) {
        ProjectVisibility visibility = projectService.getProject(projectId).getVisibility();
        if (visibility == PRIVATE) {
            throw new DataValidationException("The project is private.");
        }
    }

    private List<String> getImageUrlsList(Project project) {
        if (project.getGalleryFileKeys().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The project's gallery is empty");
        }
        List<String> imageUrlsList = new ArrayList<>();
        for (String fileKey : project.getGalleryFileKeys()) {
            try {
                String presignedUrl = amazonS3Service.generatePresignedUrl(fileKey);
                imageUrlsList.add(presignedUrl);
            } catch (Exception e) {
                log.error("Failed to generate presigned URL for image with key: " + fileKey + " - " + e.getMessage());
            }
        }
        return imageUrlsList;
    }
}
