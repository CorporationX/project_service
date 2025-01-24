package faang.school.projectservice.service.impl;

import com.amazonaws.services.kms.model.NotFoundException;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.dto.ProjectRequestDto;
import faang.school.projectservice.dto.ProjectResponseDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Override
    @Transactional
    public ProjectResponseDto save(ProjectRequestDto projectDto) {
        validateProject(projectDto);
        Project projectForSaving = projectMapper.projectRequestDtoToEntity(projectDto);
        projectForSaving.setStatus(ProjectStatus.CREATED);
        Project projectEntity = projectRepository.save(projectForSaving);
        return projectMapper.projectEntityToProjectResponseDto(projectEntity);
    }

    @Override
    public List<ProjectResponseDto> findAllByFilter(ProjectFilterDto filter) {
        String name = filter.nameFilter();
        String status = filter.statusFilter();
        if (!status.isBlank()) {
            ProjectStatus projectStatus = getProjectStatusFromString(filter.statusFilter());
            status = projectStatus.name();
        }

        List<Project> result;
        if (!name.isBlank() && !status.isBlank()) {
            result = projectRepository.findAllByNameAndStatus(name, status);
        } else if (!name.isBlank()) {
            result = projectRepository.findAllByNameContaining(name);
        } else if (!status.isBlank()) {
            result = projectRepository.findAllByStatus(status);
        } else {
            result = projectRepository.findAll();
        }
        return projectMapper.projectEntitiesToProjectResponseDtos(result);
    }

    @Override
    @Transactional
    public ProjectResponseDto update(Long id, ProjectRequestDto projectDto) {
        Project project = projectRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(String.format("There is no project with id:%d in database", id)));
        if (Objects.nonNull(projectDto.description())) {
            project.setDescription(projectDto.description());
        }
        if (Objects.nonNull(projectDto.status())) {
            project.setStatus(getProjectStatusFromString(projectDto.status()));
        }
        project.setUpdatedAt(LocalDateTime.now());
        projectRepository.save(project);
        return projectMapper.projectEntityToProjectResponseDto(project);
    }

    @Override
    public ProjectResponseDto findById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format("#ProjectServiceImpl: project with id:%d has not been found", id)));
        return projectMapper.projectEntityToProjectResponseDto(project);
    }

    @Override
    public List<ProjectResponseDto> findAll() {
        List<Project> projects = projectRepository.findAll();
        return projectMapper.projectEntitiesToProjectResponseDtos(projects);
    }

    private ProjectStatus getProjectStatusFromString(String status) {
        if (status.isBlank()) {
            throw new IllegalArgumentException("Requested status can not be empty");
        }
        for (ProjectStatus projectStatus : ProjectStatus.values()) {
            if (projectStatus.name().toLowerCase().contains(status.toLowerCase()))
                return projectStatus;
        }
        throw new IllegalArgumentException(String.format("Incorrect requested status: %s", status));
    }

    private void validateProject(ProjectRequestDto projectDto) {
        if (projectRepository.existsByOwnerIdAndName(projectDto.ownerId(), projectDto.name())) {
            throw new IllegalArgumentException(String.format(
                    "#Validation error: the same user with id:%d cannot create projects with the same name: %s"
                    , projectDto.ownerId(), projectDto.name()));
        }
    }
}
