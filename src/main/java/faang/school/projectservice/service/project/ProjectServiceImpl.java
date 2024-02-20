package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final MomentRepository momentRepository;
    private final List<ProjectFilter> filters;

    @Transactional
    public ProjectDto createSubProject(CreateSubProjectDto createSubProjectDto) {
        Project parent = projectRepository.getProjectById(createSubProjectDto.getParentId());
        Project subProject = createSubProject(createSubProjectDto, parent);
        return projectMapper.toDto(projectRepository.save(subProject));
    }

    @Transactional
    public ProjectDto updateProject(long projectId, UpdateSubProjectDto updateSubProjectDto) {
        Project project = getProject(projectId);
        if (updateSubProjectDto.getStatus() == ProjectStatus.COMPLETED
                && !isEverySubProjectComplete(project)) {
            throw new DataValidationException
                    ("The project cannot be completed while there are open subprojects");
        }

        if (updateSubProjectDto.getVisibility() == ProjectVisibility.PRIVATE
                && !isContainsSubprojects(project)) {
            project.getChildren()
                    .forEach(subProject -> subProject.setVisibility(ProjectVisibility.PRIVATE));
        }
        project.setStatus(updateSubProjectDto.getStatus());
        project.setVisibility(updateSubProjectDto.getVisibility());

        if (updateSubProjectDto.getStatus() == ProjectStatus.COMPLETED) {
            List<Long> userIds = getProjectTeamMemberIds(project);
            createSubprojectsCompletedMoment(project, userIds);
        }
        projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    public List<ProjectDto> getFilteredSubProjects(long projectId, ProjectFilterDto projectFilterDto) {
        Project project = getProject(projectId);
        if (project.getVisibility().equals(ProjectVisibility.PRIVATE)) {
            throw new DataValidationException("Object unavailable");
        }
        return filters.stream()
                .filter(projectFilter -> projectFilter.isApplicable(projectFilterDto))
                .flatMap(projectFilter -> projectFilter.apply(project.getChildren().stream(), projectFilterDto))
                .distinct()
                .map(projectMapper::toDto)
                .toList();
    }

    private Project getProject(long projectId) {
        return projectRepository.getProjectById(projectId);
    }

    private Project createSubProject(CreateSubProjectDto createSubProjectDto, Project parent) {
        Project entitySubProject = projectMapper.toEntity(createSubProjectDto);
        entitySubProject.setVisibility(parent.getVisibility());
        entitySubProject.setStatus(ProjectStatus.CREATED);
        entitySubProject.setParentProject(parent);
        return entitySubProject;
    }

    private boolean isContainsSubprojects(Project projectToUpdate) {
        return projectToUpdate.getChildren() == null || projectToUpdate.getChildren().isEmpty();
    }

    private boolean isEverySubProjectComplete(Project projectToUpdate) {
        return isContainsSubprojects(projectToUpdate) || projectToUpdate.getChildren().stream()
                .allMatch(subProject -> subProject.getStatus() == ProjectStatus.COMPLETED);
    }

    private List<Long> getProjectTeamMemberIds(Project project) {
        return Optional.ofNullable(project.getTeams())
                .orElse(Collections.emptyList())
                .stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .map(TeamMember::getId)
                .toList();
    }

    private void createSubprojectsCompletedMoment(Project project, List<Long> userIds) {
        Moment moment = momentRepository.save(buildNewMomentForProject(project, userIds));
        if (project.getMoments() == null || project.getMoments().isEmpty()) {
            project.setMoments(new ArrayList<>(Arrays.asList(moment)));
        } else {
            project.getMoments().add(moment);
        }
    }

    private Moment buildNewMomentForProject(Project project, List<Long> userIds) {
        Moment moment = new Moment();
        moment.setName(project.getName());
        moment.setUserIds(userIds);
        return moment;
    }
}