package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.CreateSubProjectDto;
import faang.school.projectservice.dto.client.SubProjectDto;
import faang.school.projectservice.dto.client.UpdateSubProjectDto;
import faang.school.projectservice.mapper.SubProjectMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class SubProjectServiceImpl implements SubProjectService {

    private final ProjectRepository projectRepository;
    private final SubProjectMapper subProjectMapper;
    private final MomentRepository momentRepository;

    @Override
    public SubProjectDto createSubProject(CreateSubProjectDto subProjectDto) {

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
        return subProjectMapper.toProjectResponseDto(projectEntity);
    }

    @Override
    public SubProjectDto updateSubProject(UpdateSubProjectDto updateSubProjectDto) {

        if (updateSubProjectDto == null) {
            throw new IllegalArgumentException("Project to update can not be null");
        }

        if (updateSubProjectDto.id() == null) {
            throw new IllegalArgumentException("Project to update id can not be null");
        }

        Project subProject = projectRepository.findById(updateSubProjectDto.id()).orElseThrow(() -> new RuntimeException("No project found to update"));

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

            Moment moment = new Moment();
            moment.getProjects().add(subProject);

            subProject.getTeams().forEach(participant -> moment
                    .getUserIds()
                    .add(participant.getId()));

            moment.setName("Выполнены все подпроекты");
            momentRepository.save(moment);
        }
        if (updateSubProjectDto.visibility().equals(ProjectVisibility.PRIVATE)
                && subProject.getChildren() != null) {
            subProject.getChildren().forEach(child -> {
                child.setVisibility(ProjectVisibility.PRIVATE);
                projectRepository.save(child);
            });
        }
        return subProjectMapper.toProjectResponseDto(subProject);
    }

    @Override
    public List<SubProjectDto> getSubprojects(Project project, String filterName, ProjectStatus filterStatus) {
        return project.getChildren().stream()
                .filter(subproject -> subproject.getVisibility().equals(ProjectVisibility.PUBLIC)
                        && subproject.getName().contains(filterName)
                        && subproject.getStatus().equals(filterStatus))
                .map(subproject -> SubProjectDto.builder()
                        .id(subproject.getId())
                        .title(subproject.getName())
                        .visibility(subproject.getVisibility())
                        .status(subproject.getStatus())
                        .subProjectIds(Collections.singletonList(project.getId()))
                        .build())
                .collect(Collectors.toList());
    }
}