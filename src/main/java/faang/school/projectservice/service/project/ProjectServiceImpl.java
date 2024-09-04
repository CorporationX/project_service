package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.exception.ExceptionMessages;
import faang.school.projectservice.filter.project.DefaultProjectFilter;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.messaging.publisher.project.ProjectEventPublisher;
import faang.school.projectservice.messaging.publisher.project.ProjectViewEventPublisher;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectMapper mapper;
    private final ProjectRepository projectRepository;
    private final List<ProjectFilter> userDefinedProjectFilters;
    private final List<DefaultProjectFilter> defaultProjectFilters;
    private final ProjectViewEventPublisher projectViewEventPublisher;
    private final ProjectEventPublisher projectEventPublisher;

    @Override
    @Transactional
    public ProjectDto createProject(ProjectDto projectDto) {
        var isDuplicateProject = projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName());
        if (isDuplicateProject) {
            throw new IllegalArgumentException(ExceptionMessages.PROJECT_ALREADY_EXISTS_FOR_OWNER_ID);
        }
        Project savedProject;
        try {
            var projectToBeSaved = mapper.toEntity(projectDto);
            if (projectDto.getParentProjectId() != null) {
                var parentProject = projectRepository.getProjectById(projectDto.getParentProjectId());
                projectToBeSaved.setParentProject(parentProject);
            }
            savedProject = projectRepository.save(projectToBeSaved);
        } catch (DataIntegrityViolationException e) {
            throw new PersistenceException(ExceptionMessages.FAILED_PERSISTENCE, e);
        }

        projectEventPublisher.toEventAndPublish(savedProject.getId());
        return mapper.toDto(savedProject);
    }

    @Override
    @Transactional
    public ProjectDto updateProject(long id, ProjectUpdateDto projectUpdateDto) {
        var projectToUpdate = projectRepository.getProjectById(id);
        var updatedProject = mapper.update(projectUpdateDto, projectToUpdate);
        updatedProject.setUpdatedAt(LocalDateTime.now());
        projectRepository.save(updatedProject);
        return mapper.toDto(updatedProject);
    }

    @Override
    public ProjectDto retrieveProject(long id) {
        var projectById = projectRepository.getProjectById(id);
        projectViewEventPublisher.toEventAndPublish(id);
        return mapper.toDto(projectById);
    }

    @Override
    public List<ProjectDto> getAllProjects() {
        return mapper.toDtoList(retrieveProjects());
    }

    private List<Project> retrieveProjects() {
        List<Project> allProjects;
        try {
            allProjects = projectRepository.findAll();
        } catch (Exception e) {
            log.error("Error occurred while fetching all projects", e);
            throw new PersistenceException(ExceptionMessages.FAILED_RETRIEVAL, e);
        }
        return allProjects;
    }

    @Override
    public List<ProjectDto> filterProjects(ProjectFilterDto filters) {
        List<Project> allProjects = retrieveProjects();
        var userDefinedFiltersApplied = applyUserDefinedFilters(filters, allProjects);
        var defaultFiltersApplied = applyDefaultFilters(userDefinedFiltersApplied);
        return mapper.toDtoList(defaultFiltersApplied);
    }

    private List<Project> applyUserDefinedFilters(ProjectFilterDto filters, List<Project> allProjects) {
        var userDefinedFilteringStream = allProjects.stream();
        for (ProjectFilter filter : userDefinedProjectFilters) {
            if (filter.isApplicable(filters)) {
                userDefinedFilteringStream = filter.apply(userDefinedFilteringStream, filters);
            }
        }
        return userDefinedFilteringStream.toList();
    }

    private HashSet<Project> applyDefaultFilters(List<Project> userDefinedFiltersApplied) {
        var defaultFiltersApplied = new HashSet<Project>();
        for (DefaultProjectFilter filter : defaultProjectFilters) {
            defaultFiltersApplied.addAll(filter.apply(userDefinedFiltersApplied.stream()).toList());
        }
        return defaultFiltersApplied;
    }
}
