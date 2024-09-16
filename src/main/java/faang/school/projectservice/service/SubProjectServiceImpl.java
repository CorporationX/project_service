package faang.school.projectservice.service;

import faang.school.projectservice.dto.CreateSubProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.util.CannotCreatePrivateProjectForPublicParent;
import faang.school.projectservice.util.ChildrenNotFinishedException;
import faang.school.projectservice.util.ParentProjectMusNotBeNull;
import faang.school.projectservice.util.RootProjectsParentMustNotBeNull;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static faang.school.projectservice.model.ProjectStatus.CANCELLED;
import static faang.school.projectservice.model.ProjectStatus.COMPLETED;

@Getter
@Service
public class SubProjectServiceImpl implements SubProjectService {
    private ProjectRepository repository;
    private ProjectMapper projectMapper;

    @Override
    public CreateSubProjectDto createSubProject(Project project) throws RootProjectsParentMustNotBeNull,
            CannotCreatePrivateProjectForPublicParent, ParentProjectMusNotBeNull {
        if (project.getParentProject() == null) {
            throw new ParentProjectMusNotBeNull();
        }
        if (project.getParentProject().getParentProject() == null) {
            throw new RootProjectsParentMustNotBeNull();
        }
        if (project.getParentProject().getVisibility() == ProjectVisibility.PUBLIC
                && project.getVisibility() == ProjectVisibility.PRIVATE) {
            throw new CannotCreatePrivateProjectForPublicParent();
        }
        repository.getProjectById(project.getParentProject().getId());

        var savedProject = repository.save(project);
        return projectMapper.toDTO(savedProject);
    }

    @Override
    public CreateSubProjectDto refreshSubProject(Project subProject) throws ChildrenNotFinishedException {
        var subproject = repository.getProjectById(subProject.getId());

        var project = ifStatusIsComletedCheckThatChildrensAreCompleted
                .andThen(setStatusAndTime)
                .andThen(assignTeamMemberMoment)
                .andThen(setVisibility)
                .apply(subproject);

        return projectMapper.toDTO(repository.save(project));

    }

    private static List<Project> getAllSubProjects(Project project) {
        return Stream.concat(
                Stream.of(project),
                Optional.ofNullable(project.getChildren())
                        .orElse(Collections.emptyList())
                        .stream()
                        .flatMap(child -> getAllSubProjects(child).stream())
        ).toList();
    }

    private final Function<Project, Project> ifStatusIsComletedCheckThatChildrensAreCompleted = (project) -> {
        if (project.getStatus() == ProjectStatus.COMPLETED) {
            var notFinishedChildren = getAllSubProjects(project).stream()
                    .filter(child -> child.getStatus() != COMPLETED && child.getStatus() != CANCELLED).toList();

            if (!notFinishedChildren.isEmpty()) {
                StringBuilder builder = new StringBuilder();
                notFinishedChildren.forEach(child -> {
                    builder.append(child.getId()).append(" status ").append(child.getStatus());
                });
                throw new ChildrenNotFinishedException(builder.toString());
            }
        }
        return project;
    };

    private final Function<Project, Project> setStatusAndTime = (project) -> {
        project.setStatus(project.getStatus());
        project.setUpdatedAt(LocalDateTime.now());
        return project;
    };

    private final Function<Project, Project> assignTeamMemberMoment = (project) -> {
        var teamMembers = Optional.ofNullable(project.getTeams())
                .orElseGet(Collections::emptyList)
                .stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .map(TeamMember::getId)
                .toList();


        Optional.ofNullable(project.getMoments())
                .orElseGet(Collections::emptyList)
                .forEach(moment -> {
                    moment.setUserIds(teamMembers);
                });
        return project;
    };

    private final Function<Project, Project> setVisibility = project -> {
        if (project.getVisibility() == ProjectVisibility.PRIVATE) {
            getAllSubProjects(project).forEach(proj -> {
                proj.setVisibility(ProjectVisibility.PRIVATE);
            });
        } else {
            project.setVisibility(project.getVisibility());
        }
        return project;
    };
}
