package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.ProjectValidator;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Slf4j
@Service
@AllArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final UserContext userContext;
    private final ProjectValidator projectValidator;
    private final List<ProjectFilter> projectFilters;

    @Transactional(readOnly = true)
    public List<ProjectDto> findAll() {
        List<Project> projects = projectRepository.findAll();
        return projectMapper.entitiesToDtos(projects);
    }

    @Transactional
    public ProjectDto createProject(ProjectDto projectDto) {
        if (projectDto.getOwnerId() == 0) {
            projectDto.setId(userContext.getUserId());
        }
        projectValidator.validateProjectByOwnerWithNameOfProject(projectDto);
        projectDto.setStatus(ProjectStatus.CREATED);
        Project project = projectMapper.dtoToEntity(projectDto);
        return projectMapper.entityToDto(projectRepository.save(project));
    }

    @Transactional
    public ProjectDto updateProject(ProjectDto updatedProjectDto) {
        existById(updatedProjectDto.getId());
        Project project = projectRepository.getProjectById(updatedProjectDto.getId());
        projectMapper.updateDtoToEntity(updatedProjectDto, project);
        return projectMapper.entityToDto(projectRepository.save(project));
    }

    @Transactional(readOnly = true)
    public ProjectDto findById(Long id) {
        existById(id);
        return projectMapper.entityToDto(projectRepository.getProjectById(id));
    }

    @Transactional(readOnly = true)
    public List<ProjectDto> getAllProjectByFilters(ProjectFilterDto projectFilter) {
        Stream<Project> projects = projectRepository.findAll().stream();
        Stream<Project> filtredProjects = projectFilters.stream()
                .filter(filter -> filter.isApplicable(projectFilter))
                .flatMap(filter -> filter.apply(projects, projectFilter));
        Predicate<Project> filterByVisibility = project -> !project.getVisibility().equals(ProjectVisibility.PRIVATE)
                || project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .anyMatch(teamMember -> teamMember.getId().equals(userContext.getUserId()));
        return filtredProjects
                .filter(filterByVisibility)
                .map(projectMapper::entityToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public boolean existById(Long id) {
        if (!projectRepository.existsById(id)) {
            String msg = "Project with id:%d doesn't exist";
            log.error(msg, id);
            throw new EntityNotFoundException(String.format(msg, id));
        }
        return projectRepository.existsById(id);
    }
}
