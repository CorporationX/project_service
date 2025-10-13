package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Override
    public ProjectDto create(ProjectCreateDto projectDto) {
        // здесь валидация
        Project project = projectMapper.toModel(projectDto);
        Project savedProject = projectRepository.save(project);

        return projectMapper.toDto(savedProject);
    }

    @Transactional
    @Override
    public void update(ProjectUpdateDto projectDto, long projectId) {
        // здесь валидация

        Project project = findProjectById(projectId);
        projectMapper.updateModel(projectDto, project);
    }

    @Override
    public ProjectDto getById(long projectId) {
        Project project = findProjectById(projectId);
        return projectMapper.toDto(project);
    }

    @Override
    public List<ProjectDto> getAll() {
        List<Project> projects = projectRepository.findAll();
        return projectMapper.toDtoList(projects);
    }

    @Override
    public List<ProjectDto> getByFilter(ProjectFilterDto filtersDto) {
        Pageable pageable = PageRequest.of(
                filtersDto.getPage(),
                filtersDto.getSize(),
                Sort.by(Sort.Direction.fromString(filtersDto.getSortDirection()), filtersDto.getSortBy()));
        Page<Project> projectPage = projectRepository.findAll(pageable);

        return projectPage.map(projectMapper::toDto)
                .getContent()
                .stream()
                .filter(projectDto -> applyFilters(projectDto, filtersDto))
                .toList();
    }

    @Override
    public void delete(long projectId) {
        projectRepository.deleteById(projectId);
    }

    private Project findProjectById(long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + projectId));
    }

    private boolean applyFilters(ProjectDto projectDto, ProjectFilterDto filters) {
        if (filters.getName() != null
                && !projectDto.getName().equals(filters.getName())) {
            return false;
        }

        if (filters.getOwnerId() != null
                && !projectDto.getOwnerId().equals(filters.getOwnerId())) {
            return false;
        }

        if (filters.getParentProjectId() != null
                && !projectDto.getParentProjectId().equals(filters.getParentProjectId())) {
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