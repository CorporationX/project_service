package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filters.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectJpaRepository projectJpaRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMapper mapper;
    private final ProjectRepository projectRepository;
    private final List<ProjectFilter> filters;

    public ProjectDto create(ProjectDto projectDto) {
        if (projectRepository.existsByOwnerUserIdAndName(projectDto.getOwner().getUserId(), projectDto.getName())) {
            throw new DataValidationException("This project already exist");
        }

        projectDto.setStatus(ProjectStatus.CREATED);
        projectDto.setCreatedAt(LocalDateTime.now());
        projectDto.setUpdatedAt(LocalDateTime.now());
        return mapper.toDto(projectRepository.save(mapper.toEntity(projectDto)));
    }

    public ProjectDto updateStatusAndDescription(ProjectDto projectDto, Long id) {
        if (id == null) {
            throw new DataValidationException("Project doesn't exist");
        }
        Project projectById = projectRepository.getProjectById(id);
        projectById.setStatus(projectDto.getStatus());
        projectById.setDescription(projectDto.getDescription());
        projectById.setUpdatedAt(LocalDateTime.now());

        Project project = mapper.toEntity(projectDto);
        return mapper.toDto(projectRepository.save(project));
    }

    public List<ProjectDto> getProjectByNameAndStatus(ProjectFilterDto projectFilterDto, long userId) {
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

    public ProjectDto getProjectById(ProjectDto projectDto) {
        Project projectById = projectRepository.getProjectById(projectDto.getId());
        return mapper.toDto(projectById);
    }

    public void deleteProjectById(Long id) {
        projectJpaRepository.deleteById(id);
    }
}
