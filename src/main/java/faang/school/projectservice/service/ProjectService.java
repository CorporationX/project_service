package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.project_filter.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final List<ProjectFilter> projectFilter;

    public ProjectDto createProject(ProjectDto projectDto) {
        if (projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())) {
            throw new DataValidationException("Project " + projectDto.getName() + " already exists");
        }

        projectDto.setStatus(ProjectStatus.CREATED);
        projectDto.setCreatedAt(LocalDateTime.now());
        return projectMapper.toDto(projectRepository.save(projectMapper.toEntity(projectDto)));
    }

    public ProjectDto updateProject(Long id, ProjectDto projectDto) {
        validateProjectExists(id);
        Project project = projectRepository.getProjectById(id);
        projectMapper.update(projectDto, project);
        return projectMapper.toDto(projectRepository.save(project));
    }

    public List<ProjectDto> getAllProjectsByStatus(Long id, ProjectDto projectDto) {
        validateProjectExists(id);
        Stream<Project> projectStream = projectRepository.findAll().stream()
                .filter(project ->  project.getVisibility().equals(ProjectVisibility.PUBLIC)
                        || (project.getTeams().stream()
                        .anyMatch(team -> team.getTeamMembers().stream()
                                .anyMatch(teamMember -> teamMember.getId().equals(id)))
                        && project.getVisibility().equals(ProjectVisibility.PRIVATE)));

        List<ProjectFilter> projectFilterList = projectFilter.stream()
                .filter(filter -> filter.isApplicable(projectDto))
                .toList();
        for (ProjectFilter project : projectFilterList) {
            projectStream = project.apply(projectStream, projectDto);
        }
        return projectStream.map(projectMapper::toDto).toList();
    }

    public List<ProjectDto> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(projectMapper::toDto)
                .toList();
    }

    public ProjectDto getProjectById(Long id) {
        validateProjectExists(id);
        return projectMapper.toDto(projectRepository.getProjectById(id));
    }

    private void validateProjectExists(long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new DataValidationException("Project with this id = " + projectId + " does not exist");
        }
    }
}
