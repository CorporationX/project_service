package faang.school.projectservice.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filters.ProjectFilter;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.util.validator.FileConverter;
import faang.school.projectservice.util.validator.CoverHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {
    private final ProjectJpaRepository projectJpaRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMapper mapper;
    private final List<ProjectFilter> filters;
    @Value("${services.s3.bucketName}")
    private String bucketName;
    private final AmazonS3 s3Client;
    private final FileConverter convertFile;
    private final CoverHandler coverHandler;

    @Transactional
    public ProjectDto create(ProjectDto projectDto) {
        if (projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())) {
            throw new DataValidationException("This project already exist");
        }
        projectDto.setStatus(ProjectStatus.CREATED);
        projectDto.setCreatedAt(LocalDateTime.now());
        projectDto.setVisibility(ProjectVisibility.PUBLIC);
        Project project = mapper.toEntity(projectDto);
        Project save = projectRepository.save(project);
        return mapper.toDto(save);
    }

    @Transactional
    public ProjectDto update(ProjectDto projectDto, long id) {
        Project projectById = projectRepository.getProjectById(id);
        projectById.setStatus(projectDto.getStatus());
        projectById.setDescription(projectDto.getDescription());
        projectById.setUpdatedAt(LocalDateTime.now());

        projectRepository.save(projectById);
        return mapper.toDto(projectById);
    }

    public List<ProjectDto> getByFilters(ProjectFilterDto projectFilterDto, long userId) {
        Stream<Project> projects = getAvailableProjectsForCurrentUser(userId).stream();

        List<ProjectFilter> listApplicableFilters = filters.stream()
                .filter(projectFilter -> projectFilter.isApplicable(projectFilterDto)).toList();
        for (ProjectFilter listApplicableFilter : listApplicableFilters) {
            projects = listApplicableFilter.apply(projects, projectFilterDto);
        }
        List<Project> listResult = projects.toList();
        return listResult.stream().map(mapper::toDto).toList();
    }

    private List<Project> getAvailableProjectsForCurrentUser(long userId) {
        List<Project> projects = projectRepository.findAll();
        List<Project> availableProjects = new ArrayList<>(projects.stream()
                .filter(project -> project.getVisibility() == ProjectVisibility.PUBLIC)
                .toList());
        List<Project> privateProjects = projects.stream()
                .filter(project -> project.getVisibility() == ProjectVisibility.PRIVATE)
                .toList();

        for (Project privateProject : privateProjects) {
            boolean isUserInPrivateProjectTeam = privateProject.getTeams().stream()
                    .anyMatch(team -> team.getTeamMembers().stream()
                            .anyMatch(teamMember -> teamMember.getUserId() == userId));
            if (isUserInPrivateProjectTeam) {
                availableProjects.add(privateProject);
            }
        }
        return availableProjects;
    }

    public List<ProjectDto> getAllProject() {
        List<Project> allProjects = projectRepository.findAll();
        return allProjects.stream()
                .map(project -> mapper.toDto(project))
                .collect(Collectors.toList());
    }

    public ProjectDto getProjectById(Long userId) {
        Project projectById = projectRepository.getProjectById(userId);
        if (projectById == null) {
            throw new EntityNotFoundException("This project is null");
        }
        return mapper.toDto(projectById);
    }

    public void deleteProjectById(Long id) {
        projectJpaRepository.deleteById(id);
    }

    @Transactional
    public String uploadFile(long projectId, MultipartFile file) {
        Project projectById = projectRepository.getProjectById(projectId);
        byte[] bytes = coverHandler.resizeCover(file);
        String fileName = getFileName(file);
        File resizedFile = convertFile.convert(bytes, fileName);
        putFile(file, resizedFile, fileName);
        projectById.setCoverImageId(fileName);
        projectRepository.save(projectById);
        return fileName;
    }

    @Transactional
    public void deleteFile(long projectId) {
        Project projectById = projectRepository.getProjectById(projectId);
        String coverImageId = projectById.getCoverImageId();
        deleteCover(coverImageId);
        projectById.setCoverImageId(null);
        projectRepository.save(projectById);
    }

    private void deleteCover(String coverImageId) {
        try {
            s3Client.deleteObject(bucketName, coverImageId);
        } catch (AmazonClientException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void putFile(MultipartFile file, File convertedFile, String fileName) {
        long fileSize = file.getSize();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(fileSize);
        metadata.setContentType(file.getContentType());
        s3Client.putObject(new PutObjectRequest(bucketName, fileName, convertedFile));
    }

    private String getFileName(MultipartFile file) {
        return System.currentTimeMillis() + "_" + file.getOriginalFilename();
    }
}
