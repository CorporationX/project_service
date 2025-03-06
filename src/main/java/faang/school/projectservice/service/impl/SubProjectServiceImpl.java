package faang.school.projectservice.service.impl;

import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectFilterDto;
import faang.school.projectservice.dto.subproject.SubProjectResponseDto;
import faang.school.projectservice.dto.subproject.UpdateSubProjectDto;
import faang.school.projectservice.filter.subproject.SubProjectFilter;
import faang.school.projectservice.mapper.SubProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.SubProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubProjectServiceImpl implements SubProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectService projectService;
    private final SubProjectMapper subProjectMapper;
    private final List<SubProjectFilter> subProjectFilters;

    public SubProjectResponseDto createSubProject(CreateSubProjectDto subProjectDto) {
        log.debug("Creating subproject with parentId: {}", subProjectDto.parentId());

        if (subProjectDto.parentId() == null) {
            throw new IllegalArgumentException("Parent id cannot be null");
        }

        Project parentProject = projectService.getProject(subProjectDto.parentId());
        if (parentProject == null) {
            throw new IllegalArgumentException("Parent project not found");
        }

        if (parentProject.getVisibility().equals(ProjectVisibility.PRIVATE)) {
            throw new IllegalStateException("Parent project is private");
        }

        Project subProjectToSave = subProjectMapper.toProjectEntity(subProjectDto);
        if (subProjectToSave == null) {
            log.error("Failed to map CreateSubProjectDto to Project: {}", subProjectDto);
            throw new IllegalStateException("Mapped subProject is null");
        }

        subProjectToSave.setParentProject(parentProject);

        Project savedProject = projectRepository.save(subProjectToSave);
        log.info("Subproject created successfully with id: {}", savedProject.getId());

        return subProjectMapper.toSubProjectResponseDto(savedProject);
    }

    @Override
    public SubProjectResponseDto updateSubProject(Long id, UpdateSubProjectDto updateSubProjectDto) {
        log.debug("Updating subproject with id: {}", id);

        if (updateSubProjectDto == null) {
            throw new IllegalArgumentException("Project to update can not be null");
        }
        if (id == null) {
            throw new IllegalArgumentException("Project to update id can not be null");
        }
        Project subProject = projectService.getProject(id);
        if (subProject == null) {
            throw new IllegalArgumentException("Subproject cannot be found");
        }
        Project subProjectValidated = validateProjectStatus(subProject);
        log.debug("Subproject with id={} updated successfully", id);
        return subProjectMapper.toSubProjectResponseDto(subProjectValidated);
    }

    private Specification<Project> getSubProjectSpecification(SubProjectFilterDto filter) {
        return subProjectFilters.stream()
                .filter(spec -> spec.isApplicable(filter))
                .map(spec -> spec.apply(filter))
                .reduce(Specification::and)
                .orElse(null);
    }

    @Override
    public List<SubProjectResponseDto> findAllByFilter(SubProjectFilterDto filter) {
        log.debug("Finding all subprojects by filter");
        Specification<Project> spec = getSubProjectSpecification(filter);
        log.debug("Found {} subprojects by filter", projectRepository.findAll(spec).size());
        return subProjectMapper.toSubProjectResponseDtos(projectRepository.findAll(spec));
    }

    @Override
    public SubProjectResponseDto findById(Long id) {
        log.debug("Finding subproject by id: {}", id);
        Project project = projectService.getProject(id);
        log.debug("Found subproject with id: {}", id);
        return subProjectMapper.toSubProjectResponseDto(project);
    }

    @Override
    public List<SubProjectResponseDto> findAll() {
        log.debug("Finding all subprojects");
        List<Project> projects = projectRepository.findAll();
        log.debug("Found {} subprojects", projects.size());
        return subProjectMapper.toSubProjectResponseDtos(projects);
    }

    private Project validateProjectStatus(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("Project cannot be null");
        }

        if (project.getStatus().equals(ProjectStatus.COMPLETED)) {
            throw new IllegalArgumentException("Project is completed");
        }

        if (project.getVisibility().equals(ProjectVisibility.PRIVATE) && project.getChildren() != null) {
            project.getChildren().forEach(child -> {
                child.setVisibility(ProjectVisibility.PRIVATE);
                projectRepository.save(child);
            });
        }
        return project;
    }
}
