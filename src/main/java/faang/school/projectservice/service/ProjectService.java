package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.ProjectValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final ProjectValidator projectValidator;
    private final List<ProjectFilter> projectFilters;

    @Transactional
    public ProjectDto createProject(ProjectDto projectDto) {
        projectValidator.checkIfProjectExists(projectDto);
        log.info("Project creation started {}", projectDto.getName());
        Project projectEntity = projectMapper.toEntity(projectDto);
        projectEntity.setStatus(ProjectStatus.CREATED);
        return projectMapper.toDto(projectRepository.save(projectEntity));
    }

    @Transactional(readOnly = true)
    public List<ProjectDto> getProjectsByFilter(ProjectFilterDto filters) {
        List<Project> projectList = projectRepository.findAll();
        log.info("Project stream started filtering {}", projectList);
        return projectFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(projectList, filters))
                .map(projectMapper:: toDto)
                .toList();
    }

    @Transactional
    public ProjectDto updateProject(Long projectId, ProjectDto projectDto) {
        Project projectEntity = projectRepository.getProjectById(projectId);
        if (!projectDto.getName().isBlank()) {
            projectEntity.setName(projectDto.getName());
        }
        if (!projectDto.getDescription().isBlank()) {
            projectEntity.setDescription(projectDto.getDescription());
        }
        if (projectDto.getStatus() != null) {
            projectEntity.setStatus(projectDto.getStatus());
        }
        log.info("Project update {}, Start saving to database", projectDto.getName());
        return projectMapper.toDto(projectRepository.save(projectEntity));
    }

    @Transactional
    public List<ProjectDto> getAllProjects() {
        log.info("Getting all projects");
        return projectRepository.findAll()
                .stream()
                .map(projectMapper::toDto)
                .toList();
    }
    @Transactional(readOnly = true)
    public ProjectDto getProjectById(Long projectId) {
        Project event = projectRepository.getProjectById(projectId);
        return projectMapper.toDto(event);
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return projectRepository.existsById(id);
    }


}