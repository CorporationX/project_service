package faang.school.projectservice.service;

import faang.school.projectservice.dto.CreateSubProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.util.ChildrenNotFinishedException;
import faang.school.projectservice.util.ThrowingConsumer;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static faang.school.projectservice.model.ProjectStatus.CANCELLED;
import static faang.school.projectservice.model.ProjectStatus.COMPLETED;

@Getter
@Service
public class SubProjectServiceImpl implements SubProjectService {
    private ProjectRepository repository;
    private ProjectMapper projectMapper;


    public CreateSubProjectDto createSubProject(Project project) {
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
                .apply(subproject);


        return projectMapper.toDTO(repository.save(project));
    }

    private static List<Project> getAllProjects(Project project) {
        return Stream.concat(
                Stream.of(project),  // Добавляем текущий проект
                Optional.ofNullable(project.getChildren())  // Handle the case where getChildren() might be null
                        .orElse(Collections.emptyList())    // If null, return an empty list
                        .stream()
                        .flatMap(child -> getAllProjects(child).stream())
        ).toList();
    }

    private Function<Project, Project> ifStatusIsComletedCheckThatChildrensAreCompleted = (project) -> {
        if (project.getStatus() == ProjectStatus.COMPLETED) {
            var notFinishedChildren = getAllProjects(project).stream()
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

    private Function<Project, Project> setStatusAndTime = (project) -> {
        project.setStatus(project.getStatus());
        project.setUpdatedAt(LocalDateTime.now());
        return project;
    };

    private Function<Project, Project> assignTeamMemberMoment = (project) -> {
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
}
