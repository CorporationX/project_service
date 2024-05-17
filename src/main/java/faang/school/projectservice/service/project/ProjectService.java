package faang.school.projectservice.service.project;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.NoProjectsInTheDatabase;
import faang.school.projectservice.exception.ProjectAlreadyExistsException;
import faang.school.projectservice.exception.ProjectDoesNotExistInTheDatabase;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.filters.ProjectFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static faang.school.projectservice.model.ProjectStatus.CREATED;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    private final ProjectMapper projectMapper;

    private final UserServiceClient userServiceClient;

    private final List<ProjectFilter> projectFilters;

    public ProjectDto createProject(ProjectDto projectDto) {
        if (projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())) {
            throw new ProjectAlreadyExistsException("Проект с именем " + projectDto.getName() + " уже существует");
        }
        Project project = projectMapper.toProject(projectDto, CREATED, projectDto.getDescription());
        project = projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    public ProjectDto updateProject(ProjectDto projectDto, ProjectStatus projectStatus, String description) {
        if (projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())) {
            throw new ProjectDoesNotExistInTheDatabase("Проект с именем " + projectDto.getName() + " не найден");
        }
        Project project = projectMapper.toProject(projectDto, projectStatus, description);
        project.setUpdatedAt(LocalDateTime.now());
        project = projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    public List<ProjectDto> getAllProjects() {
        if (projectRepository.findAll() == null) {
            throw new NoProjectsInTheDatabase("Список проектов пуст");
        }
        List<Project> projects = projectRepository.findAll();
        return projectMapper.toDto(projects);
    }

    public ProjectDto findProjectById(Long projectId) {
        Project project = projectRepository.getProjectById(projectId);
        return projectMapper.toDto(project);
    }

    public List<ProjectDto> findAllByFilter(ProjectFilterDto projectFilterDto, Long userId) {
        UserDto currentUser = userServiceClient.getUser(userId);
        List<Project> allProjects = projectRepository.findAll();
        Stream<ProjectDto> projectDtoStream = allProjects.stream()
                .map(projectMapper::toDto);
        for (ProjectFilter filter : projectFilters) {
            if (filter.isApplicable(projectFilterDto)) {
                projectDtoStream = filter.filter(projectDtoStream, projectFilterDto);
            }
        }
        return projectDtoStream
                .filter(projectDto -> isProjectVisibleToUser(projectMapper.toProject(projectDto), currentUser))
                .toList();
    }

    public boolean isProjectVisibleToUser(Project project, UserDto user) {
        if (project.getVisibility() == ProjectVisibility.PUBLIC) {
            return true;
        } else if (project.getVisibility() == ProjectVisibility.PRIVATE && user != null) {
            for (Team team : project.getTeams()) {
                for (TeamMember teamMember : team.getTeamMembers()) {
                    if (teamMember.getUserId().equals(user.getId())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}


