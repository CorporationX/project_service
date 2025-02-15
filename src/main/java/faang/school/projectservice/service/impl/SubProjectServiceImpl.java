package faang.school.projectservice.service.impl;

import faang.school.projectservice.dto.moment.MomentCreateRequestDto;
import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectFilterDto;
import faang.school.projectservice.dto.subproject.SubProjectResponseDto;
import faang.school.projectservice.dto.subproject.UpdateSubProjectDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.subproject.SubProjectFilter;
import faang.school.projectservice.mapper.SubProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.SubProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class SubProjectServiceImpl implements SubProjectService {

    private final ProjectRepository projectRepository;
    private final SubProjectMapper subProjectMapper;
    private final List<SubProjectFilter> subProjectFilters;

    @Override
    public SubProjectResponseDto createSubProject(CreateSubProjectDto subProjectDto) {

        if (subProjectDto.parentId() == null) {
            throw new IllegalArgumentException("Parent id can not be null");
        }
        Project parentProject = projectRepository.findById(subProjectDto.parentId())
                .orElseThrow(() -> new RuntimeException("Parent project not found"));

        ProjectVisibility parentVisibility = parentProject.getVisibility();
        if (parentVisibility.equals(ProjectVisibility.PRIVATE)) {
            throw new RuntimeException("Parent project is private");
        }

        Project subProjectToSave = subProjectMapper.toProjectEntity(subProjectDto);
        if (subProjectToSave == null) {
            throw new IllegalStateException("Mapped subProject is null");
        }
        subProjectToSave.setStatus(ProjectStatus.CREATED);
        Project projectEntity = projectRepository.save(subProjectToSave);
        return subProjectMapper.toSubProjectResponseDto(projectEntity);
    }

    @Override
    public SubProjectResponseDto updateSubProject(Long id, UpdateSubProjectDto updateSubProjectDto) {

        if (updateSubProjectDto == null) {
            throw new IllegalArgumentException("Project to update can not be null");
        }

        if (id == null) {
            throw new IllegalArgumentException("Project to update id can not be null");
        }

        Project subProject = projectRepository.findById(id).orElseThrow(() -> new RuntimeException("No project found to update"));

        Optional.ofNullable(subProject.getChildren())
                .ifPresent(children -> children.forEach(project -> {
                    if (!project.getStatus().equals(subProject.getStatus())) {
                        throw new RuntimeException("Project status not same as subprojects statuses");
                    }
                }));

        if (subProject.getChildren() != null &&
                subProject.getChildren().stream()
                        .allMatch(project ->
                                project.getStatus().equals(ProjectStatus.CANCELLED))) {

            MomentCreateRequestDto.builder()
                    .name("Проект закрыт")
                    .teamMemberIds(subProject.getTeams().stream().map(Team::getId).toList())
                    .build();
        }
        if (updateSubProjectDto.visibility().equals(ProjectVisibility.PRIVATE)
                && subProject.getChildren() != null) {
            subProject.getChildren().forEach(child -> {
                child.setVisibility(ProjectVisibility.PRIVATE);
                projectRepository.save(child);
            });
        }
        return subProjectMapper.toSubProjectResponseDto(subProject);
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
        Specification<Project> spec = getSubProjectSpecification(filter);
        return subProjectMapper.toSubProjectResponseDtos(projectRepository.findAll(spec));
    }

    @Override
    public SubProjectResponseDto findById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("#SubProjectServiceImpl: project with id:%d has not been found", id)));
        return subProjectMapper.toSubProjectResponseDto(project);
    }

    @Override
    public List<SubProjectResponseDto> findAll() {
        List<Project> projects = projectRepository.findAll();
        return subProjectMapper.toSubProjectResponseDtos(projects);
    }
}