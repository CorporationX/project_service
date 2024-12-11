package faang.school.projectservice.service.project;


import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.filters.ProjectFilter;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.exception.project.StorageSizeExceededException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.MultipartImage.MultipartImage;
import faang.school.projectservice.service.project.s3.S3Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final S3Service s3Service;
    private static final List<Integer> IMAGE_DIMENSIONS = List.of(1080, 566);
    private final ProjectMapper projectMapper;


    public ProjectDto createProject(ProjectDto projectDto) {
        log.info("Creating project with name: {} for owner ID: {}", projectDto.getName(), projectDto.getOwnerId());
        validateProjectNameUniqueness(projectDto.getOwnerId(), projectDto.getName());

        Project project = Project.builder()
                .name(projectDto.getName())
                .description(projectDto.getDescription())
                .ownerId(projectDto.getOwnerId())
                .status(ProjectStatus.CREATED)
                .visibility(projectDto.getVisibility() != null ? projectDto.getVisibility() : ProjectVisibility.PUBLIC)
                .createdAt(LocalDateTime.now())
                .build();

        project = projectRepository.save(project);
        log.info("Project created with ID: {}", project.getId());
        return projectMapper.toDto(project);
    }


    public ProjectDto updateProject(Long projectId, ProjectDto projectDto) {
        log.info("Updating project with ID: {}", projectId);
        Project project = projectRepository.getProjectById(projectId);

        project.setName(projectDto.getName());
        project.setDescription(projectDto.getDescription());
        project.setVisibility(projectDto.getVisibility() != null ? projectDto.getVisibility() : project.getVisibility());

        if (projectDto.getStatus() != null) {
            project.setStatus(projectDto.getStatus());
        }

        project.setUpdatedAt(LocalDateTime.now());
        project = projectRepository.save(project);
        log.info("Project with ID: {} updated successfully", projectId);
        return projectMapper.toDto(project);
    }

    public List<ProjectDto> findProjects(String name, ProjectStatus status, ProjectVisibility visibility) {
        log.info("Finding projects with filters - Name: {}, Status: {}, Visibility: {}", name, status, visibility);
        ProjectFilter filter = new ProjectFilter(name, status, visibility);
        return projectRepository.findAll().stream()
                .filter(filter::apply)
                .map(projectMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ProjectDto> getAllProjects() {
        log.info("Retrieving all projects");
        return projectRepository.findAll().stream()
                .map(projectMapper::toDto)
                .collect(Collectors.toList());
    }

    public ProjectDto getProjectById(Long projectId) {
        log.info("Retrieving project with ID: {}", projectId);
        Project project = projectRepository.getProjectById(projectId);
        return projectMapper.toDto(project);
    }

    public Optional<Project> findProject(Long id){
        if (id == null) {
            throw new IllegalArgumentException("Project not found");
        }

        return Optional.ofNullable(projectRepository.getProjectById(id));
    }

    @Transactional
    public String uploadImage(long projectId, MultipartFile file) {
        Project project = projectRepository.getProjectById(projectId);
        if (project == null) {
            throw new IllegalArgumentException("Project not found");
        }
        BigInteger newStorageSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));
        checkStorageSize(newStorageSize, project.getMaxStorageSize());

        String folder = project.getId() + project.getName();
        String key = s3Service.uploadCoverImage(file, folder);
        project.setStorageSize(newStorageSize);
        project.setCoverImageId(key);
        projectRepository.save(project);
        return key;
    }

    public MultipartFile validateImageResolution(MultipartFile file) throws IOException {
        if (file == null) {
            log.error("Received a request to validate image resolution with null file");
            throw new IllegalArgumentException("There is no file provided");
        }

        BufferedImage image = ImageIO.read(file.getInputStream());
        int width = image.getWidth();
        int height = image.getHeight();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int targetWidth = IMAGE_DIMENSIONS.get(0);
        int targetHeight = IMAGE_DIMENSIONS.get(1);
        int secondTargetHeight = IMAGE_DIMENSIONS.get(0);
        boolean resized = false;

        if (width > targetWidth || height > targetHeight && width > height) {
            resizeImage(file, targetWidth, targetHeight, outputStream);
        } else if (width > targetWidth || height > secondTargetHeight && width == height) {
            resizeImage(file, targetWidth, secondTargetHeight, outputStream);
        }

        ByteArrayInputStream byteArrayInputStream;
        if (resized) {
            byteArrayInputStream = new ByteArrayInputStream(outputStream.toByteArray());
        } else {
            byteArrayInputStream = new ByteArrayInputStream(file.getBytes());
        }

        return new MultipartImage(
                file.getBytes(),
                file.getName(),
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize(),
                byteArrayInputStream);
    }

    private void checkStorageSize(BigInteger newStorageSize, BigInteger maxStorageSize) {
        if (newStorageSize.compareTo(maxStorageSize) > 0) {
            log.error("Received a request to upload an image that exceeds the total storage");
            throw new StorageSizeExceededException("Storage size exceeded! Choose a smaller image.");
        }
    }

    private void resizeImage(MultipartFile file,
                                     int targetWidth,
                                     int targetHeight,
                                     ByteArrayOutputStream outputStream) throws IOException {
        Thumbnails.of(file.getInputStream())
                .size(targetWidth, targetHeight)
                .keepAspectRatio(true)
                .toOutputStream(outputStream);
    }

    private void validateProjectNameUniqueness(Long ownerId, String name) {
        if (projectRepository.existsByOwnerUserIdAndName(ownerId, name)) {
            log.warn("Project with name: {} already exists for owner ID: {}", name, ownerId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Project with the same name already exists for this owner.");
        }
    }
}
