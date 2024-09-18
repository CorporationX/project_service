package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.dto.subproject.ProjectFilterDto;
import faang.school.projectservice.filter.subproject.ProjectFilter;
import faang.school.projectservice.mapper.subproject.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.exception.subproject.SubProjectNotFinished;
import faang.school.projectservice.validator.subproject.SubProjectValidator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static faang.school.projectservice.model.ProjectStatus.CANCELLED;
import static faang.school.projectservice.model.ProjectStatus.COMPLETED;

@Getter
@Service
@RequiredArgsConstructor
public class SubProjectServiceImpl implements SubProjectService {
    private final ProjectRepository repository;
    private final ProjectMapper projectMapper;
    private final SubProjectValidator validator;
    private final List<ProjectFilter> projectFilters;

    @Override
    public ProjectDto createSubProject(SubProjectDto projectDto) {
        var project = repository.getProjectById(projectDto.getParentProjectId());
        validator.validateSubProjectVisibility(project.getVisibility(),projectDto.getVisibility());

        var savedProject = repository.save(project);
        return projectMapper.toDto(savedProject);
    }

    @Override
    public ProjectDto updateSubProject(long projectId, SubProjectDto subProject) throws SubProjectNotFinished {
        var subproject = repository.getProjectById(projectId);

        var project = ifStatusIsComletedCheckThatChildrensAreCompleted
                .andThen(setTime)
                .andThen(assignTeamMemberMoment)
                .andThen(setVisibility)
                .apply(subproject);

        return projectMapper.toDto(repository.save(project));
    }

    @Override
    public List<ProjectDto> getAllSubProjectsWithFiltr(Long projectId, ProjectFilterDto filtrDto) {
        Project project1 = repository.getProjectById(projectId);
        Stream<Project> allMatchedSubProjects = project1.getChildren().stream()
                .filter(subProject -> subProject.getVisibility() == ProjectVisibility.PUBLIC);

        for (ProjectFilter projectFilter : projectFilters) {
            allMatchedSubProjects = projectFilter.filter(allMatchedSubProjects, filtrDto);
        }
        return allMatchedSubProjects.map(projectMapper::toDto).toList();
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
                throw new SubProjectNotFinished(builder.toString());
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
