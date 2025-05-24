package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Data
@Service
public class ProjectServiceImpl implements ProjectService {

    private final List<ProjectFilter> filters;
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Override
    public ProjectDto create(ProjectDto projectDto) {
        validateTitleUniqueness(projectDto);
        ProjectDto completedDto = setDefaultCreationFields(projectDto);
        Project project = projectMapper.toProjectEntity(completedDto);
        return projectMapper.toProjectDto(projectRepository.save(project));
    }

    @Override
    public ProjectDto update(ProjectDto changingProjectDto) {
        Project existingProject = projectRepository.findById(changingProjectDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("No project with this id has been found"));
        Long ownerId = existingProject.getOwnerId();
        if (!Objects.equals(ownerId, changingProjectDto.getOwnerId())) {
            throw new DataValidationException("Projects can be changed only be theirs owners");
        }
        if (null != changingProjectDto.getNewOwnerId()){
            changingProjectDto.setOwnerId(changingProjectDto.getNewOwnerId());
            changingProjectDto.setNewOwnerId(null);
        }
        projectMapper.update(changingProjectDto, existingProject);
        existingProject.setUpdatedAt(LocalDateTime.now());
        projectRepository.save(existingProject);
        return projectMapper.toProjectDto(existingProject);
    }

    @Override
    public List<ProjectDto> getFilteredProjects(long userId, ProjectFilterDto dto) {
        Stream<Project> projects = projectRepository.findAll().stream();
        projects = projects
                .filter(project -> {
                    if (project.getVisibility().equals(ProjectVisibility.PRIVATE)) {
                        return isMemberOfPrivateProject(userId, project);
                    }
                    return true;
                });
        for (ProjectFilter filter : filters) {
            if (filter.isApplicable(dto)) {
                projects = filter.apply(projects, dto);
            }
        }

        return projects
                .map(projectMapper::toProjectDto)
                .toList();
    }

    @Override
    public ProjectDto getProjectById(long userId, long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("No project with this id has been found"));
        if (project.getVisibility() == ProjectVisibility.PRIVATE) {
            if (!isMemberOfPrivateProject(userId, project)) {
                throw new DataValidationException("All privet projects are visible only for members");
            }
        }
        return projectMapper.toProjectDto(project);
    }

    private boolean isMemberOfPrivateProject(long userId, Project project) {
        return (project.getOwnerId() == userId
                || Stream.ofNullable(project.getTeams()).flatMap(List::stream)
                .flatMap(team -> team.getTeamMembers().stream())
                .anyMatch(teamMember -> teamMember.getId() == userId));
    }

    private void validateTitleUniqueness(ProjectDto projectDto) {
        String projectName = projectDto.getName();
        long projectOwnerId = projectDto.getOwnerId();
        ProjectFilterDto dtoForValidation = ProjectFilterDto.builder()
                .ownerId(projectOwnerId)
                .name(projectName)
                .build();
        List<ProjectDto> sameNamedProjects = getFilteredProjects(projectOwnerId, dtoForValidation);
        if (!sameNamedProjects.isEmpty()) {
            if (sameNamedProjects.stream()
                    .noneMatch(sameNamedProject ->
                            sameNamedProject.getStatus().equals(ProjectStatus.CANCELLED))) {
                throw new DataValidationException
                        (String.format("User with id = %d already has a project named %s", projectOwnerId, projectName));
            }
        }
    }

    private ProjectDto setDefaultCreationFields(ProjectDto dto){
        if (dto.getVisibility() == null) {
            dto.setVisibility(ProjectVisibility.PUBLIC);
        }
        dto.setStatus(ProjectStatus.CREATED);
        return dto;
    }
}
