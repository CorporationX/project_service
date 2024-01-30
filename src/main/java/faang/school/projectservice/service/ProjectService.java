package faang.school.projectservice.service;


import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final List<ProjectFilter> filters;

    @Transactional
    public ProjectDto createSubProject(CreateSubProjectDto createSubProjectDto) {
        Project parent = projectRepository.getProjectById(createSubProjectDto.getParentId());
        Project entitySubProject = projectMapper.toEntity(createSubProjectDto);
//        entitySubProject.setVisibility(parent.getVisibility());
        entitySubProject.setStatus(ProjectStatus.CREATED);
//        entitySubProject.setParentProject(parent);
        Project subProject = projectRepository.save(entitySubProject);
        return projectMapper.toDto(subProject);
    }


    @Transactional
    public ProjectDto updateProject(long projectId, UpdateSubProjectDto updateSubProjectDto) {
        Project projectToUpdate = getProject(projectId);
        if (updateSubProjectDto.getStatus() == ProjectStatus.COMPLETED
                && !checkingAllSubProjectIsComplete(projectToUpdate)) {
            throw new DataValidationException
                    ("Проект невозможно завершить,пока имеются открытые подпроекты");
        }
        if (updateSubProjectDto.getVisibility() == ProjectVisibility.PRIVATE
                && !checkingListForNullAndEmpty(projectToUpdate)) {
            projectToUpdate.getChildren()
                    .forEach(subProject -> subProject.setVisibility(ProjectVisibility.PRIVATE));
        }
//        projectMapper.updateToEntity(projectToUpdate, updateSubProjectDto);
        projectToUpdate.setStatus(updateSubProjectDto.getStatus());
        projectToUpdate.setVisibility(updateSubProjectDto.getVisibility());

        if (updateSubProjectDto.getStatus() == ProjectStatus.COMPLETED) {
            List<Long> userIds = getProjectTeamMemberIds(projectToUpdate);
            createMoment(projectToUpdate, userIds);
        }
        projectRepository.save(projectToUpdate);
        return projectMapper.toDto(projectToUpdate);
    }


    @Transactional
    public List<ProjectDto> getFilteredPublicSubProjects(long projectId, ProjectFilterDto projectFilterDto) {
        Project project = getProject(projectId);
        if (project.getVisibility().equals(ProjectVisibility.PRIVATE)) {
            throw new DataValidationException("Объект недоступен");
        }
        Stream<Project> visibileProjects = filterPublicSubProjects(project);
        for (ProjectFilter filter : filters) {
            if (filter.isApplicable(projectFilterDto)) {
                visibileProjects = filter.apply(visibileProjects, projectFilterDto);
            }
        }
        return visibileProjects .map(projectMapper::toDto).toList();
    }

    private Stream<Project> filterPublicSubProjects(Project project) {
        return project.getChildren().stream()
                .filter(subProject -> subProject.getVisibility().equals(ProjectVisibility.PUBLIC));
//                .filter(subProject -> subProject.getName().equalsIgnoreCase(project.getName()))

    }


    private Project getProject(long projectId) {
        return projectRepository.getProjectById(projectId);
    }

    private boolean checkingListForNullAndEmpty(Project projectToUpdate) {
        return projectToUpdate.getChildren() == null || projectToUpdate.getChildren().isEmpty();
    }

    private boolean checkingAllSubProjectIsComplete(Project projectToUpdate) {
        return checkingListForNullAndEmpty(projectToUpdate) || projectToUpdate.getChildren().stream()
                .allMatch(subProject -> subProject.getStatus() == ProjectStatus.COMPLETED);
    }

    private List<Long> getProjectTeamMemberIds(Project projectToUpdate) {
        return Optional.ofNullable(projectToUpdate.getTeams())
                .orElse(Collections.emptyList())
                .stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .map(TeamMember::getId)
                .toList();
    }

    private void createMoment(Project projectToUpdate, List<Long> userIds) {
        Moment moment = new Moment();
        moment.setName(projectToUpdate.getName());
        moment.setUserIds(userIds);
        if (checkingListForNullAndEmpty(projectToUpdate)) {
            projectToUpdate.setMoments(new ArrayList<>(Arrays.asList(moment)));
        } else {
            projectToUpdate.getMoments().add(moment);
        }
    }
}
//        return  filters.stream()
//                .filter(projectFilter -> projectFilter.isApplicable(projectFilterDto))
//                .flatMap(projectFilter -> projectFilter.apply(visibileProjects.stream(), projectFilterDto))
//                .distinct()
//                .map(projectMapper::toDto)
//                .toList();
//    }