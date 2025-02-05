package faang.school.projectservice.service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import faang.school.projectservice.config.S3.S3Config;
import faang.school.projectservice.exception.ImageResizeException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ImageResizer imageResizer;
    private final S3Config s3Config;
    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Transactional
    public Project createProject(Project project, Long ownerId) {
        validateProjectNameUniqueness(ownerId, project.getName());
        project.setOwnerId(ownerId);
        project.setStatus(ProjectStatus.CREATED);
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        return projectRepository.save(project);
    }

    @Transactional
    public Project createSubProject(Project subProject, Long ownerId) {
        Project parentProject = findProjectById(subProject.getParentProject().getId());
        subProject.setParentProject(parentProject);
        subProject.setOwnerId(ownerId);
        subProject.setStatus(ProjectStatus.CREATED);
        subProject.setCreatedAt(LocalDateTime.now());
        subProject.setUpdatedAt(LocalDateTime.now());
        return projectRepository.save(subProject);
    }

    @Transactional
    public Project updateProject(Project project) {
        Project existingProject = findProjectById(project.getId());
        existingProject.setName(project.getName());
        existingProject.setDescription(project.getDescription());
        existingProject.setUpdatedAt(LocalDateTime.now());
        return projectRepository.save(existingProject);
    }

    @Transactional
    public Project updateSubProject(Project subProject) {
        Project existingSubProject = findProjectById(subProject.getId());
        existingSubProject.setName(subProject.getName());
        existingSubProject.setDescription(subProject.getDescription());
        existingSubProject.setUpdatedAt(LocalDateTime.now());
        return projectRepository.save(existingSubProject);
    }

    @Transactional(readOnly = true)
    public Page<Project> getProjects(String name, ProjectStatus status, Long userId, Pageable pageable) {
        return projectRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Project> getSubProjects(Long parentProjectId, String name, ProjectStatus status, Pageable pageable) {
        return projectRepository.findByParentProjectId(parentProjectId, pageable);
    }

    @Transactional(readOnly = true)
    public Project getProjectById(Long projectId, Long userId) {
        return findProjectById(projectId);
    }

    private void validateProjectNameUniqueness(Long ownerId, String name) {
        if (projectRepository.existsByOwnerIdAndName(ownerId, name)) {
            throw new IllegalArgumentException("Project with the same name already exists");
        }
    }

    @Transactional(readOnly = true)
    private Project findProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
    }

    @Transactional(readOnly = true)
    public Project getProjectById(long projectId, long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        if (!isProjectVisible(project, userId)) {
            throw new IllegalArgumentException("You don't have access to this project");
        }

        return project;
    }

    @Transactional(readOnly = true)
    public List<Project> getProjectsByIds(List<Long> projectIds, long userId) {
        List<Project> projects = projectRepository.findAllById(projectIds);

        projects.forEach(project -> {
            if (!isProjectVisible(project, userId)) {
                throw new IllegalArgumentException("You don't have access to project by id " + project.getId());
            }
        });

        return projects;
    }

    @Transactional(readOnly = true)
    public List<Long> getUserIdsByProjectIds(List<Long> projectIds) {
        return projectRepository.getUserIdsByProjectIds(projectIds);
    }

    private boolean isProjectVisible(Project project, Long userId) {
        return project.getVisibility() == ProjectVisibility.PUBLIC || project.getOwnerId().equals(userId);
    }

    @Transactional
    public void uploadProjectCover(Long projectId, MultipartFile file) {
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new MaxUploadSizeExceededException(file.getSize());
        }

        byte[] resizedImage = null;
        try {
            resizedImage = imageResizer.resizeImage(file.getBytes(), 1080, 566);
        } catch (IOException e) {
            throw new ImageResizeException("Failed to resize image " + file.getOriginalFilename());
        }

        String objectName = "project-" + projectId + "-cover.jpg";
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(resizedImage.length);
        objectMetadata.setContentType(file.getContentType());

        s3Config.amazonS3Client().putObject(
                bucketName,
                objectName,
                new ByteArrayInputStream(resizedImage),
                objectMetadata);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        project.setCoverImageId(objectName);
        projectRepository.save(project);
    }

    @Transactional
    public void deleteCover(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        String objectName = project.getCoverImageId();

        if (objectName != null) {
            s3Config.amazonS3Client().deleteObject(bucketName, objectName);
        } else {
            throw new IllegalArgumentException("Cover image not found");
        }

        project.setCoverImageId(null);
        projectRepository.save(project);
    }


}