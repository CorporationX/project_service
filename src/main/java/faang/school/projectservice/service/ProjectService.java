package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.exception.DataAlreadyExistingException;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.DataNotFoundException;
import faang.school.projectservice.exception.PrivateAccessException;
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

    public ProjectCreateDto create(ProjectCreateDto projectCreateDto) {
        projectCreateDto.setName(processTitle(projectCreateDto.getName()));
        long ownerId = projectCreateDto.getOwnerId();
        String projectName = projectCreateDto.getName();

        if (projectRepository.existsByOwnerUserIdAndName(ownerId, projectName)) {
            throw new DataAlreadyExistingException(String
                    .format("User with id: %d already exist project %s", ownerId, projectName));
        }

        Project project = projectMapper.toModel(projectCreateDto);
        LocalDateTime now = LocalDateTime.now();
        project.setCreatedAt(now);
        project.setUpdatedAt(now);
        project.setStatus(ProjectStatus.CREATED);

        if (projectCreateDto.getVisibility() == null) {
            project.setVisibility(ProjectVisibility.PUBLIC);
        }
        projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    public ProjectCreateDto update(ProjectUpdateDto projectCreateDto) {
        long projectId = projectCreateDto.getId();
        if (projectRepository.getProjectById(projectId) == null) {
            throw new DataNotFoundException("This Project doesn't exist");
        }
        Project projectToUpdate = projectRepository.getProjectById(projectId);
        if (projectCreateDto.getDescription() != null) {
            projectToUpdate.setDescription(projectCreateDto.getDescription());
            projectToUpdate.setUpdatedAt(LocalDateTime.now());
        }
        if (projectCreateDto.getStatus() != null) {
            projectToUpdate.setStatus(projectCreateDto.getStatus());
            projectToUpdate.setUpdatedAt(LocalDateTime.now());
        }
        projectRepository.save(projectToUpdate);
        return projectMapper.toDto(projectToUpdate);
    }

    public List<ProjectCreateDto> getProjectsWithFilter(ProjectFilterDto projectFilterDto, long userId) {
        Stream<Project> projects = getAvailableProjectsForCurrentUser(userId).stream();

        List<ProjectFilter> listApplicableFilters = filters.stream()
                .filter(projectFilter -> projectFilter.isApplicable(projectFilterDto)).toList();
        for (ProjectFilter listApplicableFilter : listApplicableFilters) {
            projects = listApplicableFilter.apply(projects, projectFilterDto);
        }
        List<Project> listResult = projects.toList();
        return listResult.stream().map(projectMapper::toDto).toList();
    }

    public List<ProjectCreateDto> getAllProjects(long userId) {
        return getAvailableProjectsForCurrentUser(userId).stream()
                .map(projectMapper::toDto)
                .toList();
    }


    public ProjectCreateDto getProjectById(long projectId, long userId) {
        Project projectById = projectRepository.getProjectById(projectId);
        boolean isUserInPrivateProjectTeam = projectById.getTeams().stream()
                .anyMatch(team -> team.getTeamMembers().stream()
                        .anyMatch(teamMember -> teamMember.getUserId() == userId));

        if (!isUserInPrivateProjectTeam) {
            throw new PrivateAccessException("This project is private");
        }
        return projectMapper.toDto(projectById);
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
