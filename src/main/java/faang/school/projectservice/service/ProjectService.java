package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.ErrorMessage;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.filter.ProjectFilter;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper mapper;
    private final ProjectFilter filter;

    @Transactional(readOnly = true)
    public List<ProjectDto> getProjectsByIds(List<Long> ids) {
        return projectRepository.findAllById(ids).stream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional
    public ProjectDto create(ProjectDto projectDto) {
        if (!projectRepository.existsByOwnerIdAndName(projectDto.getOwner().getId(), projectDto.getName())) {
            Project entity = projectRepository.save(mapper.toEntity(projectDto));
            return mapper.toDto(entity);
        }
        throw new DataValidationException(ErrorMessage.PROJECT_ALREADY_EXISTS, projectDto.getName());
    }

    public ProjectDto update(ProjectDto projectDto) {
        Project entity = projectRepository.findById(projectDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Couldn't find a project with id " + projectDto.getId()));
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

        return mapper.toDto(projectRepository.save(entity));
    }


    @Transactional(readOnly = true)
    public ProjectDto getProject(long id) {
        return mapper.toDto(projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Couldn't find a user with id " + id)));
    }

    @Transactional(readOnly = true)
    public boolean existsById(long id) {
        return projectRepository.existsById(id);
    }


    @Transactional(readOnly = true)
    public List<ProjectDto> getProjectsByFilter(ProjectFilterDto filterDto) {
        Stream<Project> projects = projectRepository.findAll().stream();
        return filter.applyFilter(projects, filterDto).map(mapper::toDto).collect(Collectors.toList());
    }
}
