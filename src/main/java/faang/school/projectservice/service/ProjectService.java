package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectViewEvent;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.publisher.ProjectViewMessagePublisher;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.ProjectValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectValidator projectServiceValidator;
    private final ProjectMapper projectMapper;
    private final ProjectRepository projectRepository;
    private final List<ProjectFilter> filters;
    private final ProjectViewMessagePublisher projectViewMessagePublisher;
    private final UserContext userContext;

    @Transactional
    public ProjectDto create(Long userId, ProjectDto projectDto) {
        projectServiceValidator.validateCreation(userId, projectDto);
        Project project = projectMapper.toEntity(projectDto);
        project.setOwnerId(userId);
        project.setStatus(ProjectStatus.CREATED);
        if (project.getVisibility() == null) {
            project.setVisibility(ProjectVisibility.PUBLIC);
        }
        return projectMapper.toDto(projectRepository.save(project));
    }

    @Transactional
    public ProjectDto update(ProjectDto projectDto) {
        projectServiceValidator.validateUpdating(projectDto);
        Project project = projectMapper.toEntity(projectDto);
        return projectMapper.toDto(projectRepository.save(project));
    }

    @Transactional(readOnly = true)
    public List<ProjectDto> findProjectsWithFilter(Long userId, ProjectFilterDto projectFilterDto) {
        List<ProjectFilter> applicableFilters = getApplicableProjectFilters(projectFilterDto);
        List<Project> filteredProjects = projectRepository.findAllAvailableProjectsByUserId(userId).toList();
        for (ProjectFilter applicableFilter : applicableFilters) {
            filteredProjects = applicableFilter.apply(filteredProjects.stream(), projectFilterDto).toList();
        }
        return projectMapper.toDtos(filteredProjects);
    }

    @Transactional(readOnly = true)
    public List<ProjectDto> findAllProjects(Long userId) {
        return projectMapper.toDtos(projectRepository
                .findAllAvailableProjectsByUserId(userId)
                .toList());
    }

    @Transactional(readOnly = true)
    public ProjectDto findById(Long userId, ProjectFilterDto projectFilterDto) {
        projectServiceValidator.validateProjectFilterDtoForFindById(projectFilterDto);
        return projectMapper.toDto(projectRepository
                .findAvailableByUserIdAndProjectId(userId, projectFilterDto.getIdPattern())
                .orElseThrow(() -> {
                    log.info("Project with id {} not found", projectFilterDto.getIdPattern());
                    return new DataValidationException("Project not found");
                }));
    }

    private List<ProjectFilter> getApplicableProjectFilters(ProjectFilterDto projectFilterDto) {
        return filters.stream()
                .filter(filter -> filter.isApplicable(projectFilterDto))
                .toList();
    }
    @Transactional
    public ProjectDto getProject(Long projectId) {
        Project project = getProjectById(projectId);
        viewProject(projectId);
        return projectMapper.toDto(project);
    }

    private void viewProject(Long projectId) {
        ProjectViewEvent projectViewEvent = ProjectViewEvent.builder()
                .projectId(projectId)
                .userId(userContext.getUserId())
                .eventTime(LocalDateTime.now())
                .build();
        projectViewMessagePublisher.publish(projectViewEvent);
    }

    /**
     * Do not use this method in Controller. Only for internal use.
     */
    @Transactional(readOnly = true)
    public Project getProjectById(Long projectId) {
        return projectRepository.getProjectById(projectId);
    }
}
