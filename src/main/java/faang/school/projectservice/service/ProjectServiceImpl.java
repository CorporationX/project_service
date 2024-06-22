package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.validation.ProjectValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final ProjectValidator projectValidator;
    private final UserContext userContext;
    private final ProjectFilterService projectFilterService;

    @Override
    public ProjectDto create(ProjectDto projectDto) {
        if (projectDto.getOwnerId() == null) {
            projectDto.setOwnerId(userContext.getUserId());
        }
        projectValidator.validateProjectByOwnerIdAndNameOfProject(projectDto);
        projectDto.setStatus(ProjectStatus.CREATED);
        Project createdProject = projectRepository.save(projectMapper.toProject(projectDto));
        return projectMapper.toDto(createdProject);
    }

    @Override
    public ProjectDto update(ProjectDto projectDto) {
        Project project = getProjectById(projectDto.getId());
        projectMapper.updateProject(projectDto, project);
        return projectMapper.toDto(projectRepository.save(project));
    }

    @Override
    public List<ProjectDto> getAll() {
        List<Project> projects = projectRepository.findAll();
        return projectMapper.toDtos(projects);
    }

    @Override
    public ProjectDto findById(long id) {
        Project project = getProjectById(id);
        return projectMapper.toDto(project);
    }

    @Override
    public List<ProjectDto> getAllByFilter(ProjectFilterDto projectFilterDto) {
        List<Project> projects = projectRepository.findAll();

        Predicate<Project> filterByVisibility = project -> !project.getVisibility().equals(ProjectVisibility.PRIVATE) || project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .anyMatch(teamMember -> teamMember.getId().equals(userContext.getUserId()));

        Stream<Project> filteredProjectsByVisibility = projects.stream()
                .filter(filterByVisibility);

        return projectFilterService.applyFilters(filteredProjectsByVisibility, projectFilterDto)
                .map(projectMapper::toDto)
                .toList();
    }

    private Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Project not found by id: %s", projectId))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserProjectOwner(long projectId, long userId) {
        log.info("Received GET request on project {} and user {}", projectId, userId);
        return projectRepository.checkUserIsProjectOwner(projectId,userId);
    }
}
