package faang.school.projectservice.service;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.s3.S3ServiceImpl;
import faang.school.projectservice.util.CoverHandler;
import faang.school.projectservice.validator.ProjectValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final List<ProjectFilter> projectFilters;
    private final ProjectValidator projectValidator;
    private final S3ServiceImpl s3Service;
    private final CoverHandler coverHandler;

    public void validateProjectId(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new EntityNotFoundException("Project with id " + projectId + " does not exist");
        }
    }

    @Transactional
    public ProjectDto createProject(ProjectDto projectDto) {
        projectValidator.existProjectValidator(projectDto.getId());
        Project project = projectMapper.toEntity(projectDto);
        project.setStatus(ProjectStatus.CREATED);
        project.setVisibility(ProjectVisibility.valueOf(projectDto.getVisibility()));
        return projectMapper.toDto(projectRepository.save(project));
    }

    @Transactional
    public ProjectDto updateProject(ProjectDto projectDto) {
        projectValidator.existProjectValidator(projectDto.getId());
        Project project = projectRepository.getProjectById(projectDto.getId());
        projectMapper.updateProjectFromDto(projectDto, project);
        project.setUpdatedAt(LocalDateTime.now());
        return projectMapper.toDto(projectRepository.save(project));
    }

    public ProjectDto findProjectById(Long id) {
        projectValidator.existProjectValidator(id);
        Project project = projectRepository.getProjectById(id);
        return projectMapper.toDto(project);
    }

    public List<ProjectDto> getAllProject() {
        return projectMapper.toDtoList(projectRepository.findAll());
    }


    public List<ProjectDto> getProjectByFilter(ProjectFilterDto filters) {
        return projectFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(projectRepository.findAll().stream(), filters))
                .map(projectMapper::toDto)
                .toList();
    }

    @Transactional
    public String addCoverProject(Long projectId, MultipartFile multipartFile) {
        Project project = projectRepository.getProjectById(projectId);

        projectValidator.validateSizeFile(multipartFile);
        coverHandler.resizeCover(multipartFile);


        String folder = "projectId_" + project.getId() + "_projectName_" + project.getName();
        String key = s3Service.uploadFile(multipartFile, folder);

        project.setCoverImageId(key);
        projectRepository.save(project);

        log.debug("Cover with key: " + multipartFile.getOriginalFilename() + " uploaded");

        return key;
    }

    @Transactional(readOnly = true)
    public InputStream getProjectCover(Long projectId) {
        return s3Service.downloadFile(projectRepository.getProjectById(projectId).getCoverImageId());
    }

    @Transactional
    public void deleteCover(Long projectId) {
        Project project = projectRepository.getProjectById(projectId);
        String key = project.getCoverImageId();
        project.setCoverImageId(null);
        projectRepository.save(project);
        s3Service.deleteFile(key);
        log.debug("Cover with key: " + key + " deleted");
    }
}