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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final List<ProjectFilter> projectFilter;

    @Transactional
    public ProjectDto createProject(ProjectDto projectDto) {
        if (projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())) {
            throw new DataValidationException("Project " + projectDto.getName() + " already exists");
        }
        log.info("Project creation started {}", projectDto.getId());
        projectDto.setStatus(ProjectStatus.CREATED);

        return projectMapper.toDto(projectRepository.save(projectMapper.toEntity(projectDto)));
    }

    @Transactional
    public ProjectDto updateProject(Long id, ProjectDto projectDto) {
        validateProjectExists(id);
        Project project = projectRepository.getProjectById(id);
        projectMapper.update(projectDto, project);
        log.info("Project update {}, Start saving to database", projectDto.getId());
        return projectMapper.toDto(projectRepository.save(project));
    }

    @Transactional(readOnly = true)
    public List<ProjectDto> getAllProjectsByStatus(Long id, ProjectDto projectDto) {
        validateProjectExists(id);
        log.info("Getting all projects by status {}", id);
        Stream<Project> projectList = projectRepository.findAll().stream()
                .filter(project -> project.getVisibility().equals(ProjectVisibility.PUBLIC)
                        || (project.getTeams().stream()
                        .anyMatch(team -> team.getTeamMembers().stream()
                                .anyMatch(teamMember -> teamMember.getId().equals(id)))
                ));
        Stream<Project> projectStream = projectList;

        log.info("Project stream started filtering {}", projectStream);
        List<ProjectFilter> projectFilterList = projectFilter.stream()
                .filter(filter -> filter.isApplicable(projectDto))
                .toList();
        for (ProjectFilter project : projectFilterList) {
            projectStream = project.apply(projectStream, projectDto);
        }
        return projectStream.map(projectMapper::toDto).toList();
    }

    @Transactional
    public List<ProjectDto> getAllProjects() {
        log.info("Getting all projects");
        return projectRepository.findAll()
                .stream()
                .map(projectMapper::toDto)
                .toList();
    }

    @Transactional
    public ProjectDto getProjectById(Long id) {
        validateProjectExists(id);
        log.info("Getting project by id {}", id);
        return projectMapper.toDto(projectRepository.getProjectById(id));
    }

    private void validateProjectExists(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new DataValidationException("Project with this id = " + projectId + " does not exist");
        }
    }
}
