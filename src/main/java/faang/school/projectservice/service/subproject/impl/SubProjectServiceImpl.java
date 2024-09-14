package faang.school.projectservice.service.subproject.impl;

import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectFilterDto;
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
    public SubProjectDto create(SubProjectDto subProjectDto) {
        Project parentProject = projectRepository.getProjectById(subProjectDto.getParentProjectId());

        if (parentProject.getVisibility() == ProjectVisibility.PUBLIC && subProjectDto.getVisibility() == ProjectVisibility.PRIVATE) {
            throw new IllegalArgumentException("You cannot create a private sub-project for a public parent project with id " + parentProject.getId());
        }

        Project subProject = subProjectMapper.toProject(subProjectDto);
        subProject.setParentProject(parentProject);
        subProject.setCreatedAt(LocalDateTime.now());
        subProject.setUpdatedAt(LocalDateTime.now());

        if (subProjectDto.getStatus() == null) {
            subProject.setStatus(ProjectStatus.CREATED);
        }

        if (subProjectDto.getVisibility() == null) {
            subProject.setVisibility(ProjectVisibility.PUBLIC);
        }

        subProject = projectRepository.save(subProject);
        log.info("Created new sub-project with id {}", subProject.getId());
        return subProjectMapper.toSubProjectDto(subProject);
    }

    @Override
    public SubProjectDto update(SubProjectDto subProjectDto) {
        Project subProject = projectRepository.getProjectById(subProjectDto.getId());
        subProject.setStatus(subProjectDto.getStatus());
        subProject.setVisibility(subProjectDto.getVisibility());

        if (!checkIfAllSubProjectsOfCurrentProjectHaveSameStatus(subProject)) {
            throw new IllegalArgumentException("You cannot update a project, because the subprojects statuses are not suitable. ID: " + subProjectDto.getId());
        }

        if (checkIfAllSubProjectsAreClosed(subProject)) {
            Moment moment = createMomentIfSubProjectClosed(subProject);
            subProject.getMoments().add(moment);
        }

        if (subProject.getVisibility() == ProjectVisibility.PRIVATE) {
            List<Project> subProjects = subProject.getChildren();
            subProjects.forEach(p -> p.setVisibility(ProjectVisibility.PRIVATE));
            subProject.setChildren(subProjects);
        }
        subProject.setUpdatedAt(LocalDateTime.now());

        subProject = projectRepository.save(subProject);
        log.info("Updated sub-project with id {}", subProject.getId());
        return subProjectMapper.toSubProjectDto(subProject);
    }

    @Override
    public List<SubProjectDto> findSubProjectsByParentId(Long parentId, SubProjectFilterDto subProjectFilter) {
        Stream<Project> subProjects = projectRepository.getProjectById(parentId).getChildren().stream()
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

    private boolean checkIfAllSubProjectsOfCurrentProjectHaveSameStatus(Project subProject) {
        List<Project> subProjects = subProject.getChildren();
        return subProjects.stream()
                .allMatch(project -> project.getStatus() == subProject.getStatus());
    }

    private boolean checkIfAllSubProjectsAreClosed(Project subProject) {
        List<Project> subProjects = subProject.getChildren();
        boolean childrenStatus = subProjects.stream()
                .allMatch(project -> project.getStatus() == ProjectStatus.COMPLETED);

        return childrenStatus && subProject.getStatus() == ProjectStatus.COMPLETED;
    }

    private Moment createMomentIfSubProjectClosed(Project subProject) {
        Moment moment = new Moment();
        moment.setName("All projects are completed");
        moment.setDate(LocalDateTime.now());
        List<Long> userIds = subProject.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream()
                        .map(TeamMember::getUserId))
                .toList();
        moment.setUserIds(userIds);
        return momentRepository.save(moment);
    }
}
