package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.SubProjectDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.exception.DataAlreadyExistingException;
import faang.school.projectservice.exception.DataNotFoundException;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.PrivateAccessException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.SubProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.filters.ProjectFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final SubProjectMapper subProjectMapper;
    private final List<ProjectFilter> filters;

    public ProjectDto create(ProjectDto projectDto) {
        validateNameAndDescription(projectDto);
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


    public ProjectDto getProjectById(long projectId, long userId) {
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

    @Transactional
    public SubProjectDto createSubProject(SubProjectDto subProjectDto) {
        checkProjectNotExist(subProjectDto);
        checkSubProjectNotPublicOnPrivateProject(subProjectDto.getParentProjectId(), subProjectDto.getVisibility(), subProjectDto.getName());
        Project subProject = subProjectMapper.toEntity(subProjectDto);
        Project parentProject = projectRepository.getProjectById(subProjectDto.getParentProjectId());
        subProject.setParentProject(parentProject);
        subProject.setStatus(ProjectStatus.CREATED);
        projectRepository.save(subProject);
        return subProjectMapper.toDto(subProject);
    }

    public LocalDateTime updateSubProject(UpdateSubProjectDto updateSubProjectDto) {
        Project projectToUpdate = projectRepository.getProjectById(updateSubProjectDto.getId());

        if (updateSubProjectDto.getStatus() == ProjectStatus.COMPLETED) {
            return closeSubProject(updateSubProjectDto, projectToUpdate);
        }
        checkUpdatedVisibility(updateSubProjectDto, projectToUpdate);
        setAllNeededFields(updateSubProjectDto, projectToUpdate);
        projectRepository.save(projectToUpdate);
        return projectToUpdate.getUpdatedAt();
    }

    public List<SubProjectDto> getProjectChildrenWithFilter(ProjectFilterDto projectFilterDto, long projectId) {
        Project project = projectRepository.getProjectById(projectId);
        Stream<Project> subProjectsStream = project.getChildren().stream();
        List<ProjectFilter> applicableFilters = filters.stream()
                .filter(projectFilter -> projectFilter.isApplicable(projectFilterDto))
                .toList();
        for (ProjectFilter filter : applicableFilters) {
            subProjectsStream = filter.apply(subProjectsStream, projectFilterDto);
        }
        return subProjectsStream.map(subProjectMapper::toDto)
                .toList();
    }

    private LocalDateTime closeSubProject(UpdateSubProjectDto updateSubProjectDto, Project projectToUpdate) {
        projectToUpdate.getChildren().forEach(this::checkProjectStatusNotCompletedOrCancelled);
        setAllNeededFields(updateSubProjectDto, projectToUpdate);
        projectRepository.save(projectToUpdate);
        return projectToUpdate.getUpdatedAt();
    }

    private void checkUpdatedVisibility(UpdateSubProjectDto updateSubProjectDto, Project projectToUpdate) {
        ProjectVisibility newVisibility = updateSubProjectDto.getVisibility();
        String projectName = projectToUpdate.getName();

        if (newVisibility == ProjectVisibility.PUBLIC) {
            if (updateSubProjectDto.getParentProjectId() != null) {
                checkSubProjectNotPublicOnPrivateProject(updateSubProjectDto.getParentProjectId(), newVisibility, projectName);
            } else {
                checkSubProjectNotPublicOnPrivateProject(projectToUpdate.getParentProject().getId(), newVisibility, projectName);
            }
        }
        if (newVisibility == ProjectVisibility.PRIVATE) {
            makeAllSubprojectPrivate(projectToUpdate);
        }
    }

    private void makeAllSubprojectPrivate(Project project) {
        List<Project> children = project.getChildren();

        if (children != null && !children.isEmpty()) {
            children.forEach(this::makeAllSubprojectPrivate);
        }
        project.setVisibility(ProjectVisibility.PRIVATE);
        projectRepository.save(project);
    }

    private void setAllNeededFields(UpdateSubProjectDto updateSubProjectDto, Project projectToUpdate) {
        if (updateSubProjectDto.getParentProjectId() != null) {
            Project newParentProject = projectRepository.getProjectById(updateSubProjectDto.getParentProjectId());
            projectToUpdate.setParentProject(newParentProject);
        }
        if (updateSubProjectDto.getName() != null) {
            projectToUpdate.setName(updateSubProjectDto.getName());
        }
        if (updateSubProjectDto.getDescription() != null) {
            projectToUpdate.setDescription(updateSubProjectDto.getDescription());
        }
        if (updateSubProjectDto.getStatus() != null) {
            projectToUpdate.setStatus(updateSubProjectDto.getStatus());
        }
        if (updateSubProjectDto.getVisibility() != null) {
            projectToUpdate.setVisibility(updateSubProjectDto.getVisibility());
        }
    }

    private void checkSubProjectNotPublicOnPrivateProject(long parentProjectId, ProjectVisibility visibility, String projectName) {
        Project parentProject = projectRepository.getProjectById(parentProjectId);
        boolean isParentProjectPrivate = parentProject.getVisibility().equals(ProjectVisibility.PRIVATE);
        boolean isSubProjectPublic = visibility.equals(ProjectVisibility.PUBLIC);
        if (isParentProjectPrivate && isSubProjectPublic) {
            throw new DataValidationException(String.format("Public SubProject: %s, cant be with a private parent Project with id: %d", projectName, parentProject.getId()));
        }
    }

    private void checkProjectNotExist(SubProjectDto SubProjectDto) {
        if (projectRepository.existsByOwnerUserIdAndName(SubProjectDto.getOwnerId(), SubProjectDto.getName())) {
            throw new DataAlreadyExistingException(String.format("Project %s is already exist", SubProjectDto.getName()));
        }
    }

    private void checkProjectStatusNotCompletedOrCancelled(Project project) {
        if (project.getStatus() != ProjectStatus.COMPLETED && project.getStatus() != ProjectStatus.CANCELLED) {
            throw new DataValidationException("Can't close project if subProject status are not complete or cancelled");
        }
    }

    private void validateNameAndDescription(ProjectDto projectDto) {
        String name = projectDto.getName();
        String description = projectDto.getDescription();
        if (name == null || name.isBlank()) {
            throw new DataValidationException("Project can't be created with empty name");
        }
        if (description == null || description.isBlank()) {
            throw new DataValidationException("Project can't be created with empty description");
        }
    }

    public Project getProjectByIdFromRepo(Long projectId) {
        return projectRepository.getProjectById(projectId);
    }

    public boolean isProjectExist(Long projectId) {
        return projectRepository.existsById(projectId);
    }
}
