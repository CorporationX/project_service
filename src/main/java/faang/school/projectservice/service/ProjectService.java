package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.dto.ProjectUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.ProjectValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final List<ProjectFilter> projectFilters;
    private final UserContext userContext;
    private final ProjectValidator projectValidator;

    @Transactional
    public ProjectDto createProject(ProjectDto projectDto) {
        projectDto.setOwnerId(userContext.getUserId());
        if (projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())) {
            throw new DataValidationException("Project " + projectDto.getName() + " already exist");
        }
        log.info("Project creation started {}", projectDto.getName());
        var project = projectMapper.toEntity(projectDto);
        return projectMapper.toDto(projectRepository.save(project));
    }

    @Transactional
    public ProjectDto updateProject(Long id, ProjectUpdateDto projectUpdateDto) {
        var projectToUpdate = projectRepository.getProjectById(id);
        projectValidator.checkForValidOwner(userContext.getUserId(), projectToUpdate);
        if (!projectUpdateDto.getDescription().isBlank()) {
            projectToUpdate.setDescription(projectUpdateDto.getDescription());
        }
        if (projectUpdateDto.getStatus() != null) {
            projectToUpdate.setStatus(projectUpdateDto.getStatus());
        }
        log.info("Project updated {}", projectToUpdate.getName());
        projectRepository.save(projectToUpdate);
        return projectMapper.toDto(projectToUpdate);
    }

    @Transactional(readOnly = true)
    public List<ProjectDto> getAllProjectsWithFilter(ProjectFilterDto projectFilterDto) {
        List<Project> projects = projectRepository.findAll();
        return projectFilters.stream()
                .filter(projectFilter -> projectFilter.isApplicable(projectFilterDto))
                .flatMap(projectFilter -> {
                    return projectFilter.applyFilter(projectFilterDto, getFilteredProject(projects));
                })
                .distinct()
                .map(projectMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProjectDto> getAllProjects() {
        List<Project> projects = projectRepository.findAll();
        return getFilteredProject(projects)
                .map(projectMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectDto getProjectById(Long id) {
        var project = projectRepository.getProjectById(id);
        return projectMapper.toDto(projectValidator.checkIfUserIsMember(userContext.getUserId(), project));
    }

    private Stream<Project> getFilteredProject(List<Project> projects) {
        return projects.stream()
                .map(project -> projectValidator.checkIfUserIsMember(userContext.getUserId(), project))
                .filter(Objects::nonNull);
    }
}
