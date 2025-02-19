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
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class SubProjectServiceImpl implements SubProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectService projectService;
    private final SubProjectMapper subProjectMapper;
    private final List<SubProjectFilter> subProjectFilters;

    @Override
    public SubProjectResponseDto createSubProject(CreateSubProjectDto subProjectDto) {
        log.info("Creating subproject with parentId: {}", subProjectDto.parentId());

        if (subProjectDto.parentId() == null) {
            throw new IllegalArgumentException("Parent id can not be null");
        }
        Project parentProject = projectService.getProject(subProjectDto.parentId());

        if (parentProject.getVisibility().equals(ProjectVisibility.PRIVATE)) {
            throw new RuntimeException("Parent project is private");
        }

        Project subProjectToSave = subProjectMapper.toProjectEntity(subProjectDto);
        if (subProjectToSave == null) {
            throw new IllegalStateException("Mapped subProject is null");
        }
        subProjectToSave.setStatus(ProjectStatus.CREATED);
        Project projectEntity = projectRepository.save(subProjectToSave);
        log.info("Subproject created successfully with id: {}", projectEntity.getId());
        return subProjectMapper.toSubProjectResponseDto(projectEntity);
    }

    @Override
    public SubProjectResponseDto updateSubProject(Long id, UpdateSubProjectDto updateSubProjectDto) {
        log.info("Updating subproject with id: {}", id);

        if (updateSubProjectDto == null) {
            throw new IllegalArgumentException("Project to update can not be null");
        }
        if (id == null) {
            throw new IllegalArgumentException("Project to update id can not be null");
        }

        Project subProject = projectService.getProject(id);
        Project subProjectValidated = projectService.validateProjectStatus(subProject);
        log.info("Subproject with id={} updated successfully", id);
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
        log.info("Finding all subprojects by filter");
        Specification<Project> spec = getSubProjectSpecification(filter);
        log.info("Found {} subprojects by filter", projectRepository.findAll(spec).size());
        return subProjectMapper.toSubProjectResponseDtos(projectRepository.findAll(spec));
    }

    @Override
    public SubProjectResponseDto findById(Long id) {
        log.info("Finding subproject by id: {}", id);
        Project project = projectService.getProject(id);
        log.info("Found subproject with id: {}", id);
        return subProjectMapper.toSubProjectResponseDto(project);
    }

    @Override
    public List<SubProjectResponseDto> findAll() {
        log.info("Finding all subprojects");
        List<Project> projects = projectRepository.findAll();
        log.info("Found {} subprojects", projects.size());
        return subProjectMapper.toSubProjectResponseDtos(projects);
    }
}
