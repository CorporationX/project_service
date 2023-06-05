package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.filter.FilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.ErrorMessage;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.filter.Filter;
import faang.school.projectservice.service.filter.NameFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper mapper;
    private final List<Filter<Project>> filters;

    @Transactional(readOnly = true)
    public List<ProjectDto> getProjectsByIds(List<Long> ids) {
        return projectRepository.findAllByIds(ids).stream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional
    public ProjectDto create(ProjectDto projectDto) {
        if (!projectRepository.existsByOwnerIdAndName(projectDto.getOwner(), projectDto.getName())) {
            Project entity = save(mapper.toEntity(projectDto));
            return mapper.toDto(entity);
        }
        throw new DataValidationException(ErrorMessage.PROJECT_ALREADY_EXISTS, projectDto.getName());
    }

    @Transactional
    public ProjectDto update(ProjectDto projectDto) {
        Project entity = projectRepository.getProjectById(projectDto.getId());
        if (nonNull(projectDto.getName())
                && !projectRepository.existsByOwnerIdAndName(entity.getOwner().getId(), projectDto.getName())) {
            entity.setName(projectDto.getName());
        }
        if (nonNull(projectDto.getDescription())) {
            entity.setDescription(projectDto.getDescription());
        }
        if (nonNull(projectDto.getStatus())) {
            entity.setStatus(projectDto.getStatus());
        }
        entity.setUpdatedAt(LocalDateTime.now());

        return mapper.toDto(save(entity));
    }

    @Transactional
    public Project save(Project project) {
        return projectRepository.save(project);
    }

    @Transactional(readOnly = true)
    public Project getProjectById(long id) {
        return projectRepository.getProjectById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsById(long id) {
        return projectRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public List<ProjectDto> getProjectsByFilter(FilterDto filterDto) {
        List<Project> projects = projectRepository.findAll();

        for (Filter filter : filters) {
            if (filter instanceof NameFilter) {
                projects = filter.applyFilter(projects.stream().map(Project::getName), filterDto);
            }
        }

        return projects.stream().map(mapper::toDto).collect(Collectors.toList());
    }
}
