package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataAlreadyExistingException;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.DataNotFoundException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.filters.ProjectFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class ProjectService {

    private final ProjectMapper projectMapper;
    private final ProjectRepository projectRepository;
    private final List<ProjectFilter> filters;

    public ProjectDto create(ProjectDto projectDto) {
        projectDto.setName(processTitle(projectDto.getName()));
        long ownerId = projectDto.getOwnerId();
        String projectName = projectDto.getName();

        if (projectRepository.existsByOwnerUserIdAndName(ownerId, projectName)) {
            throw new DataAlreadyExistingException(String
                    .format("User with id: %d already exist project %s", ownerId, projectName));
        }

        Project project = projectMapper.toModel(projectDto);
        LocalDateTime now = LocalDateTime.now();
        project.setCreatedAt(now);
        project.setUpdatedAt(now);
        project.setStatus(ProjectStatus.CREATED);

        if (projectDto.getVisibility() == null) {
            project.setVisibility(ProjectVisibility.PUBLIC);
        }
        projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    public ProjectDto update(ProjectDto projectDto) {
        long projectId = projectDto.getId();
        if (projectRepository.getProjectById(projectId) == null) {
            throw new DataNotFoundException("This Project doesn't exist");
        }
        Project projectToUpdate = projectRepository.getProjectById(projectId);
        if (projectDto.getDescription() != null) {
            projectToUpdate.setDescription(projectDto.getDescription());
            projectToUpdate.setUpdatedAt(LocalDateTime.now());
        }
        if (projectDto.getStatus() != null) {
            projectToUpdate.setStatus(projectDto.getStatus());
            projectToUpdate.setUpdatedAt(LocalDateTime.now());
        }
        projectRepository.save(projectToUpdate);
        return projectMapper.toDto(projectToUpdate);
    }

    public List<ProjectDto> getProjectsWithFilter(ProjectFilterDto projectFilterDto, long userId) {
        Stream<Project> projects = getAvailableProjectsForCurrentUser(userId).stream();

        List<ProjectFilter> listApplicableFilters = filters.stream()
                .filter(projectFilter -> projectFilter.isApplicable(projectFilterDto)).toList();
        for (ProjectFilter listApplicableFilter : listApplicableFilters) {
            projects = listApplicableFilter.apply(projects, projectFilterDto);
        }
        List<Project> listResult = projects.toList();
        return listResult.stream().map(projectMapper::toDto).toList();
    }

    public List<ProjectDto> getAllProjects(long userId) {
        return getAvailableProjectsForCurrentUser(userId).stream()
                .map(projectMapper::toDto)
                .toList();
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

    private String processTitle(String title) {
        title = title.replaceAll("[^A-Za-zА-Яа-я0-9+-/#]", " ");
        title = title.replaceAll("[\\s]+", " ");
        return title.trim().toLowerCase();
    }
}
