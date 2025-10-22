package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.dto.subproject.UpdateSubProjectDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.mapper.SubProjectMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
@AllArgsConstructor
public class SubProjectServiceImpl implements SubProjectService {

    private final ProjectRepository projectRepository;
    private final SubProjectMapper subProjectMapper;
    private final MomentRepository momentRepository;

    @Override
    public SubProjectDto createSubProject(CreateSubProjectDto createSubProjectDto) {
        Project parentProject = projectRepository.findById(createSubProjectDto.parentProjectId())
                .orElseThrow(() -> new EntityNotFoundException("Parent project not found"));

        if (parentProject.getVisibility() == ProjectVisibility.PRIVATE
                && createSubProjectDto.visibility() == ProjectVisibility.PUBLIC) {
            throw new IllegalArgumentException("Cannot create a public subproject for a private parent project");
        }

        Project subProject = subProjectMapper.toSubProject(createSubProjectDto);
        subProject.setParentProject(parentProject);
        Project saved = projectRepository.save(subProject);
        return subProjectMapper.toSubProjectDto(saved);
    }

    @Override
    @Transactional
    public SubProjectDto updateSubProject(UpdateSubProjectDto updateSubProjectDto, Long subProjectId) {
        Project subProject = projectRepository.findById(subProjectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));

        subProject.setName(updateSubProjectDto.name());

        if (updateSubProjectDto.description() != null) {
            subProject.setDescription(updateSubProjectDto.description());
        }

        if (updateSubProjectDto.visibility() == ProjectVisibility.PRIVATE) {
            makeAllSubProjectPrivate(subProject);
        }

        subProject.setVisibility(updateSubProjectDto.visibility());

        if (updateSubProjectDto.status() == ProjectStatus.COMPLETED) {
            validateAllChildrenStatus(subProject, ProjectStatus.COMPLETED);
            createMomentIfAllSubProjectsCompleted(subProject);
        }

        subProject.setStatus(updateSubProjectDto.status());

        Project saved = projectRepository.save(subProject);
        return subProjectMapper.toSubProjectDto(saved);
    }

    @Override
    public List<SubProjectDto> getSubProjects(Long projectId, String name, ProjectStatus status) {
        Project parentProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));

        if (parentProject.getChildren() == null) {
            return List.of();
        }

        return parentProject.getChildren().stream()
                .filter(p -> name == null || p.getName().contains(name))
                .filter(p -> status == null || p.getStatus() == status)
                .filter(p -> {
                    if (parentProject.getVisibility() == ProjectVisibility.PRIVATE) {
                        return p.getVisibility() == ProjectVisibility.PRIVATE;
                    } else {
                        return p.getVisibility() == ProjectVisibility.PUBLIC;
                    }
                })
                .map(subProjectMapper::toSubProjectDto)
                .toList();
    }

    private void makeAllSubProjectPrivate(Project rootProject) {
        List<Project> allChildren = new ArrayList<>();
        collectAllChildren(rootProject, allChildren);
        allChildren.forEach(p -> p.setVisibility(ProjectVisibility.PRIVATE));
        projectRepository.saveAll(allChildren);
    }

    private void collectAllChildren(Project project, List<Project> result) {
        if (project.getChildren() == null || project.getChildren().isEmpty()) return;
        for (Project child : project.getChildren()) {
            result.add(child);
            collectAllChildren(child, result);
        }
    }

    private void forAllSubProjects(Project rootProject, Consumer<Project> action) {
        if (rootProject.getChildren() == null || rootProject.getChildren().isEmpty()) {
            return;
        }

        for (Project child : rootProject.getChildren()) {
            action.accept(child);
            forAllSubProjects(child, action);
        }
    }

    private void validateAllChildrenStatus(Project project, ProjectStatus requiredStatus) {
        forAllSubProjects(project, child -> {
            if (child.getStatus() != requiredStatus) {
                throw new IllegalStateException(
                        "Cannot complete project: subproject '" + child.getName() + "' has status " + child.getStatus()
                );
            }
        });
    }

    private void createMomentIfAllSubProjectsCompleted(Project project) {
        Moment moment = new Moment();
        moment.setName("All subprojects completed: " + project.getName());
        moment.setDescription("This moment records the completion of all subprojects of the project " + project.getName());
        moment.setDate(LocalDateTime.now());

        moment.setProjects(List.of(project));

        List<Long> participantIds = project.getChildren().stream()
                .flatMap(p -> p.getTeams().stream())
                .flatMap(team -> team.getTeamMembers().stream())
                .map(TeamMember::getUserId)
                .distinct()
                .toList();

        moment.setUserIds(participantIds);

        momentRepository.save(moment);
    }
}
