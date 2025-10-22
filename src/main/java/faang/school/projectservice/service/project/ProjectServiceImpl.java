package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Override
    public ProjectDto create(ProjectCreateDto projectDto) {
        log.info("Creating project: name='{}', ownerId={}", projectDto.getName(), projectDto.getOwnerId());
        validateCreate(projectDto);

        Project project = projectMapper.toModel(projectDto);
        project.setStatus(ProjectStatus.CREATED);
        Project savedProject = projectRepository.save(project);

        log.info("Project created: id={}, name='{}'", savedProject.getId(), savedProject.getName());
        return projectMapper.toDto(savedProject);
    }

    @Transactional
    @Override
    public ProjectDto update(ProjectUpdateDto newProjectDto, long projectId) {
        log.info("Updating project: id={}", projectId);

        Project project = findProjectById(projectId);
        validateUpdate(newProjectDto, project);
        projectMapper.updateModel(newProjectDto, project);

        log.info("Project updated: id={}", projectId);

        return projectMapper.toDto(project);
    }

    @Override
    public ProjectDto getById(long projectId) {
        log.debug("Fetching project by id: {}", projectId);
        Project project = findProjectById(projectId);
        return projectMapper.toDto(project);
    }

    @Override
    public List<ProjectDto> getByFilter(ProjectFilterDto filtersDto) {
        log.debug("Filtering projects: {}", filtersDto);
        Pageable pageable = PageRequest.of(
                filtersDto.getPage(),
                filtersDto.getSize(),
                Sort.by(Sort.Direction.fromString(filtersDto.getSortDirection()), filtersDto.getSortBy()));
        Page<Project> projectPage = projectRepository.findAll(pageable);

        List<ProjectDto> result = projectPage.map(projectMapper::toDto).getContent()
                .stream()
                .filter(projectDto -> applyFilters(projectDto, filtersDto))
                .toList();

        log.debug("Filtered {} projects out of {}", result.size(), projectPage.getTotalElements());
        return result;
    }

    @Override
    public void delete(long projectId) {
        log.info("Deleting project: id={}", projectId);
        projectRepository.deleteById(projectId);
        log.info("Project deleted: id={}", projectId);
    }

    private void validateCreate(ProjectCreateDto projectDto) {
        if (projectDto.getParentProjectId() != null && !projectRepository.existsById(projectDto.getParentProjectId())) {
            log.warn("Create validation failed: parent project not found, parentId={}",
                    projectDto.getParentProjectId());
            throw new IllegalArgumentException("Parent project does not exist");
        }

        if (projectRepository.existsByOwnerIdAndName(projectDto.getOwnerId(), projectDto.getName())) {
            log.warn("Create validation failed: duplicate project name='{}' for ownerId={}",
                    projectDto.getName(), projectDto.getOwnerId());
            throw new IllegalArgumentException("Project with this name already exists");
        }
    }

    private void validateUpdate(ProjectUpdateDto newProjectDto, Project project) {
        if (project.getStatus() == ProjectStatus.CANCELLED || project.getStatus() == ProjectStatus.COMPLETED) {
            log.warn("Update validation failed: invalid status={}, projectId={}",
                    project.getStatus(), project.getId());
            throw new IllegalArgumentException("Cannot update project in status " + project.getStatus());
        }
        if (newProjectDto.getMaxStorageSize() != null && project.getStorageSize() != null) {
            log.warn("Update validation failed: maxSize={} < currentSize={}, projectId={}",
                    newProjectDto.getMaxStorageSize(), project.getStorageSize(), project.getId());
            if (project.getStorageSize().compareTo(newProjectDto.getMaxStorageSize()) > 0) {
                throw new IllegalArgumentException("Max storage size cannot be less than current usage");
            }
        }
    }

    private Project findProjectById(long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> {
                    log.error("Project not found: id={}", projectId);
                    return new IllegalArgumentException("Project not found with id: " + projectId);
                });
    }

    private boolean applyFilters(ProjectDto projectDto, ProjectFilterDto filters) {
        if (filters.getProjectId() != null
                && !filters.getProjectId().equals(projectDto.getId())) {
            return false;
        }

        if (filters.getName() != null
                && !projectDto.getName().equals(filters.getName())) {
            return false;
        }

        if (filters.getOwnerId() != null
                && !projectDto.getOwnerId().equals(filters.getOwnerId())) {
            return false;
        }

        if (filters.getParentProjectId() != null
                && (projectDto.getParentProjectId() == null
                || !Objects.equals(projectDto.getParentProjectId(), filters.getParentProjectId()))) {
            return false;
        }

        if (filters.getStatus() != null
                && !projectDto.getStatus().equals(filters.getStatus())) {
            return false;
        }

        if (filters.getVisibility() != null
                && !projectDto.getVisibility().equals(filters.getVisibility())) {
            return false;
        }

        if (filters.getCreatedAfter() != null
                && !projectDto.getCreatedAt().isAfter(filters.getCreatedAfter())) {
            return false;
        }

        if (filters.getCreatedBefore() != null
                && !projectDto.getCreatedAt().isBefore(filters.getCreatedBefore())) {
            return false;
        }

        return true;
    }
}