package faang.school.projectservice.service.subproject.impl;

import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectFilterDto;
import faang.school.projectservice.dto.subproject.request.CreationRequest;
import faang.school.projectservice.dto.subproject.request.UpdatingRequest;
import faang.school.projectservice.mapper.subproject.SubProjectMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.subproject.SubProjectService;
import faang.school.projectservice.service.subproject.filter.SubProjectFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubProjectServiceImpl implements SubProjectService {

    private final ProjectRepository projectRepository;
    private final SubProjectMapper subProjectMapper;
    private final MomentRepository momentRepository;
    private final List<SubProjectFilter> filters;

    @Override
    public SubProjectDto create(CreationRequest creationRequest) {
        Project parentProject = projectRepository.getProjectById(creationRequest.getParentProjectId());

        if (parentProject.getVisibility() == ProjectVisibility.PUBLIC && creationRequest.getVisibility() == ProjectVisibility.PRIVATE) {
            throw new IllegalArgumentException("You cannot create a private sub-project for a public parent project with id " + parentProject.getId());
        }

        Project subProject = subProjectMapper.toProjectFromCreationRequest(creationRequest);
        subProject.setParentProject(parentProject);
        subProject.setStatus(ProjectStatus.CREATED);

        subProject = projectRepository.save(subProject);
        log.info("Created new sub-project with id {}", subProject.getId());
        return subProjectMapper.toSubProjectDto(subProject);
    }

    @Override
    public SubProjectDto update(Long projectId, UpdatingRequest updatingRequest) {
        Project subProject = projectRepository.getProjectById(projectId);
        subProjectMapper.updateProjectFromUpdateRequest(updatingRequest, subProject);
        boolean allChildrenAreFinished = subProject.getChildren().stream()
                .allMatch(this::isProjectFinished);

        if (isProjectFinished(subProject)) {
            if (!allChildrenAreFinished) {
                throw new IllegalArgumentException("You cannot update a project, " +
                        "because the subprojects statuses are not suitable. ID: " + projectId);
            }

            Moment moment = createMoment(subProject);
            subProject.getMoments().add(moment);
        }

        if (subProject.getVisibility() == ProjectVisibility.PRIVATE) {
            List<Project> subProjects = subProject.getChildren();
            subProjects.forEach(p -> p.setVisibility(ProjectVisibility.PRIVATE));
        }

        subProject = projectRepository.save(subProject);
        log.info("Updated sub-project with id {}", subProject.getId());
        return subProjectMapper.toSubProjectDto(subProject);
    }

    @Override
    public List<SubProjectDto> findSubProjectsByParentId(Long parentId, SubProjectFilterDto subProjectFilter) {
        Stream<Project> subProjects = projectRepository.findAllByParentId(parentId).stream()
                .filter(project -> project.getVisibility() != ProjectVisibility.PRIVATE);

        return filters.stream()
                .filter(filter -> filter.isApplicable(subProjectFilter))
                .reduce(subProjects,
                        (invites, filter) -> filter.apply(invites, subProjectFilter),
                        (p1, p2) -> p1
                )
                .map(subProjectMapper::toSubProjectDto)
                .toList();
    }

    private boolean isProjectFinished(Project project) {
        return project.getStatus() == ProjectStatus.COMPLETED || project.getStatus() == ProjectStatus.CANCELLED;
    }

    private Moment createMoment(Project project) {
        Moment moment = new Moment();
        moment.setName("All projects are completed");
        moment.setDate(LocalDateTime.now());
        List<Long> userIds = project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream()
                        .map(TeamMember::getUserId))
                .toList();
        moment.setUserIds(userIds);
        return momentRepository.save(moment);
    }
}
