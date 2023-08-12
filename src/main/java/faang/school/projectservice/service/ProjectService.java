package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectByFilterDto;
import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.exception.CheckIfProjectExists;
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
    public ProjectDto createProject(ProjectCreateDto projectCreateDto) {
        if (projectRepository.existsByOwnerUserIdAndName(projectCreateDto.getOwnerId(), projectCreateDto.getName())) {
            throw new DataValidationException("Project " + projectCreateDto.getName() + " already exists");
        }
        log.info("Project creation started {}", projectCreateDto.getName());
        projectCreateDto.setStatus(ProjectStatus.CREATED);

        return projectMapper.toDto(projectRepository.save(projectMapper.createDtoToProject(projectCreateDto)));
    }

    @Transactional
    public ProjectDto updateProject(Long id, ProjectUpdateDto projectUpdateDto) {
        validateProjectExists(id);
        Project project = projectRepository.getProjectById(id);
        projectMapper.update(projectUpdateDto, project);
        log.info("Project update {}, Start saving to database", projectUpdateDto.getName());
        return projectMapper.toDto(projectRepository.save(project));
    }

    @Transactional(readOnly = true)
    public List<ProjectDto> getAllProjectsByFilter(Long id, ProjectByFilterDto projectByFilterDto) {
        validateProjectExists(id);
        log.info("Getting all projects by status {}", id);

        Stream<Project> projectStream = projectRepository.findAll().stream()
                .filter(project1 -> project1.getVisibility().equals(ProjectVisibility.PUBLIC)
                        || (project1.getTeams().stream()
                        .anyMatch(team -> team.getTeamMembers().stream()
                                .anyMatch(teamMember -> teamMember.getId().equals(id)))
                ));

        log.info("Project stream started filtering {}", projectStream);
        List<ProjectFilter> projectFilterList = projectFilter.stream()
                .filter(filter -> filter.isApplicable(projectByFilterDto))
                .toList();
        for (ProjectFilter project : projectFilterList) {
            projectStream = project.apply(projectStream, projectByFilterDto);
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
            throw new CheckIfProjectExists("Project with this id = " + projectId + " does not exist");
        }
    }
}
