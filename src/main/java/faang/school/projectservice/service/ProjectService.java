package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.ProjectValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Slf4j
@Service
@AllArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final UserContext userContext;
    private final ProjectValidator projectValidator;
    private final List<ProjectFilter> projectFilters;
    private final TeamMemberRepository teamMemberRepository;

    @Transactional(readOnly = true)
    public List<ProjectDto> findAll() {
        List<Project> projects = projectRepository.findAll();
        return projectMapper.toDtoList(projects);
    }

    @Transactional
    public ProjectDto createProject(ProjectDto projectDto) {
        if (projectDto.getOwnerId() == 0) {
            projectDto.setOwnerId(userContext.getUserId());
        }
        projectValidator.validateProjectByOwnerWithNameOfProject(projectDto);
        projectDto.setStatus(ProjectStatus.CREATED);
        Project project = projectMapper.toEntity(projectDto);
        return projectMapper.toDto(projectRepository.save(project));
    }

    @Transactional
    public ProjectDto updateProject(ProjectDto updatedProjectDto) {
        existById(updatedProjectDto.getId());
        Project project = projectRepository.getProjectById(updatedProjectDto.getId());
        project.setName(updatedProjectDto.getName());
        project.setDescription(project.getDescription());
        project.setStatus(updatedProjectDto.getStatus());
        project.setVisibility(updatedProjectDto.getVisibility());
        return projectMapper.toDto(projectRepository.save(project));
    }

    @Transactional(readOnly = true)
    public ProjectDto findById(Long id) {
        existById(id);
        return projectMapper.toDto(projectRepository.getProjectById(id));
    }

    @Transactional(readOnly = true)
    public List<ProjectDto> getAllProjectByFilters(ProjectFilterDto projectFilterDto) {
        Stream<Project> projects = projectRepository.findAll().stream();
        for (ProjectFilter projectFilter : projectFilters) {
            projects = projectFilter.filter(projects, projectFilterDto);
        }
        Predicate<Project> filterByVisibility = project -> !project.getVisibility().equals(ProjectVisibility.PRIVATE)
                || project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .anyMatch(teamMember -> teamMember.getId().equals(userContext.getUserId()));
        return projects
                .filter(filterByVisibility)
                .map(projectMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public boolean existById(Long id) {
        if (!projectRepository.existsById(id)) {
            String msg = "Project with id:%d doesn't exist";
            log.error(msg, id);
            throw new EntityNotFoundException(String.format(msg, id));
        }
        return projectRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public List<Project> findDifferentProjects(List<Project> projectsFromDataBase, List<Long> newProjectIds) {
        List<Long> existingProjectIds = projectsFromDataBase.stream()
                .map(Project::getId)
                .toList();
        newProjectIds.removeAll(existingProjectIds);
        return convertProjectsByIds(newProjectIds);
    }

    @Transactional(readOnly = true)
    public List<Project> getNewProjects(List<Long> userIds) {
        Set<Project> projects = new HashSet<>();
        userIds.forEach(userId -> {
            List<TeamMember> teamMembers = teamMemberRepository.findByUserId(userId);
            teamMembers.forEach(teamMember -> projects.add(teamMember.getTeam().getProject()));
        });
        return new ArrayList<>(projects);
    }

    private List<Project> convertProjectsByIds(List<Long> projectIds) {
        return projectIds.stream()
                .map(projectRepository::getProjectById)
                .toList();
    }
}
