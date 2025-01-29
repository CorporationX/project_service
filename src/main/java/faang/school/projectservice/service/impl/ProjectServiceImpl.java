package faang.school.projectservice.service.impl;

import faang.school.projectservice.dto.ProjectCreateRequestDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.dto.ProjectResponseDto;
import faang.school.projectservice.dto.ProjectUpdateRequestDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.SpecificationFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final List<SpecificationFilter> specificationFilters;

    @Override
    public ProjectResponseDto save(ProjectCreateRequestDto projectDto) {
        validateProject(projectDto);
        Project projectForSaving = projectMapper.toProjectEntity(projectDto);
        projectForSaving.setStatus(ProjectStatus.CREATED);
        Project projectEntity = projectRepository.save(projectForSaving);
        return projectMapper.toProjectResponseDto(projectEntity);
    }

    @Override
    public List<ProjectResponseDto> findAllByFilter(ProjectFilterDto filter) {
        Specification<Project> spec = getProjectSpecification(filter);
        return projectMapper.toProjectResponseDtos(projectRepository.findAll(spec));
    }

    @Override
    public ProjectResponseDto update(Long id, ProjectUpdateRequestDto projectDto) {
        Project project = projectRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("There is no project with id:%d in database", id)));
        projectMapper.update(projectDto, project);
        project.setUpdatedAt(LocalDateTime.now());
        projectRepository.save(project);
        return projectMapper.toProjectResponseDto(project);
    }

    @Override
    public ProjectResponseDto findById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("#ProjectServiceImpl: project with id:%d has not been found", id)));
        return projectMapper.toProjectResponseDto(project);
    }

    @Override
    public List<ProjectResponseDto> findAll() {
        List<Project> projects = projectRepository.findAll();
        return projectMapper.toProjectResponseDtos(projects);
    }

    private void validateProject(ProjectCreateRequestDto projectDto) {
        if (projectRepository.existsByOwnerIdAndName(projectDto.ownerId(), projectDto.name())) {
            throw new IllegalArgumentException(String.format(
                    "#Validation error: the same user with id:%d cannot create projects with the same name: %s",
                    projectDto.ownerId(), projectDto.name()));
        }
    }

    private Specification<Project> getProjectSpecification(ProjectFilterDto filter) {
        return specificationFilters.stream()
                .filter(spec -> spec.isApplicable(filter))
                .map(spec -> spec.apply(filter))
                .reduce((spec1, spec2) -> spec1.and(spec2))
                .orElse(null);
    }
}
