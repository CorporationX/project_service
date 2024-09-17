package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.exception.CannotCreatePrivateProjectForPublicParent;
import faang.school.projectservice.exception.ChildrenNotFinishedException;
import faang.school.projectservice.exception.ParentProjectMusNotBeNull;
import faang.school.projectservice.exception.RootProjectsParentMustNotBeNull;
import faang.school.projectservice.validator.SubProjectValidator;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
    private SubProjectValidator validator;

    @Override
    public SubProjectDto createSubProject(Long projectId) throws RootProjectsParentMustNotBeNull,
            CannotCreatePrivateProjectForPublicParent, ParentProjectMusNotBeNull {
        var project = repository.getProjectById(projectId);
        validator.validate(project);

        var savedProject = repository.save(project);
        return projectMapper.toDTO(savedProject);
    }

    @Override
    public SubProjectDto updateSubProject(SubProjectDto subProject) throws ChildrenNotFinishedException {
        var subproject = repository.getProjectById(subProject.getId());

        var project = ifStatusIsComletedCheckThatChildrensAreCompleted
                .andThen(setTime)
                .andThen(assignTeamMemberMoment)
                .andThen(setVisibility)
                .apply(subproject);

        return projectMapper.toDTO(repository.save(project));

    }

    @Override
    public List<SubProjectDto> getAllSubProjectsWithFiltr(SubProjectDto project, String nameFilter, ProjectStatus statusFilter) {
        var result = repository.findAllByIds(project.getChildren()).stream()
                .filter(child ->
                        Objects.equals(nameFilter, child.getName())
                                && statusFilter == child.getStatus()
                                && child.getVisibility() != ProjectVisibility.PRIVATE
                ).map(filteredProject -> {
                    return projectMapper.toDTO(filteredProject);
                }).toList();
        return result;
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

    private final Function<Project, Project> setTime = (project) -> {
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
        }
        return project;
    };
}
