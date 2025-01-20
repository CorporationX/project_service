package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.DataAlreadyExistException;
import faang.school.projectservice.exception.DataNotFoundException;
import faang.school.projectservice.exception.DataValidateException;
import faang.school.projectservice.filter.ProjectFilter;
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
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final List<ProjectFilter> projectFilters;

    public ProjectDto createProject(ProjectDto projectDto) {
        validateNameAndDescription(projectDto);
        projectDto.setName(nameAdjustment(projectDto.getName()));

        if (projectRepository.existsByOwnerIdAndName(projectDto.getOwnerId(), projectDto.getName())) {
            throw new DataAlreadyExistException("The user already has a project with this name");
        }
        projectDto.setCreatedAt(LocalDateTime.now());
        projectDto.setStatus(ProjectStatus.CREATED);

        Project project = projectMapper.toEntity(projectDto);
        projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    public ProjectDto updatedProject(ProjectDto projectDto) {
        Project projectToUpdate = projectRepository.findById(projectDto.getId())
                .orElseThrow(() -> new DataNotFoundException("This project does not found"));

        if (projectDto.getDescription() != null &&
                !Objects.equals(projectDto.getDescription(), projectToUpdate.getDescription())) {
            projectToUpdate.setDescription(projectDto.getDescription());
            projectToUpdate.setUpdatedAt(LocalDateTime.now());
        }

        if (projectDto.getStatus() != null &&
                !Objects.equals(projectDto.getStatus(), projectToUpdate.getStatus())) {
            projectToUpdate.setStatus(projectDto.getStatus());
            projectToUpdate.setUpdatedAt(LocalDateTime.now());
        }

        projectRepository.save(projectToUpdate);
        return projectMapper.toDto(projectToUpdate);
    }

    public List<ProjectDto> getProjectWithFilters(ProjectFilterDto filterDto, long userId) {
        Stream<Project> allAvailableProject = getAvailableProjectForUser(userId).stream();
        projectFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .forEach(filter -> filter.applyFilter(allAvailableProject, filterDto));
        List<Project> projectsAfterFilters = allAvailableProject.toList();
        return projectsAfterFilters.stream().map(projectMapper::toDto).toList();
    }

    public List<ProjectDto> getAllProject(long userId) {
        return getAvailableProjectForUser(userId).stream()
                .map(projectMapper::toDto)
                .toList();
    }

    public ProjectDto getProjectId(long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new DataNotFoundException("This project does not found"));
        return projectMapper.toDto(project);
    }

    public String nameAdjustment(String title) {
        return title.replaceAll("[^A-Za-zА-Яа-я0-9#/+-]", " ")
                .replaceAll("[\\s]+", " ")
                .trim()
                .toLowerCase();
    }

    private List<Project> getAvailableProjectForUser(long userId) {
        List<Project> allProject = projectRepository.findAll();
        List<Project> availableProject = new ArrayList<>(allProject.stream()
                .filter(project -> project.getVisibility() == ProjectVisibility.PUBLIC)
                .toList());
        List<Project> privateProject = new ArrayList<>(allProject.stream()
                .filter(project -> project.getVisibility() == ProjectVisibility.PRIVATE)
                .toList());

        for (Project project : privateProject) {
            boolean isAvailableCurrentUser = project.getTeams()
                    .stream()
                    .anyMatch(team -> team.getTeamMembers()
                            .stream()
                            .anyMatch(teamMember -> teamMember.getUserId() == userId));
            if (isAvailableCurrentUser) {
                availableProject.add(project);
            }
        }
        return availableProject;
    }

    private void validateNameAndDescription(ProjectDto projectDto) {
        if (projectDto.getName().isBlank()) {
            throw new DataValidateException("Name project can not be blank");
        }

        if (projectDto.getDescription().isBlank()) {
            throw new DataValidateException("Description project can not be blank");
        }
    }
}
