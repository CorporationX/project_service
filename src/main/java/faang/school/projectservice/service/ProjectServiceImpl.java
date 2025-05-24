package faang.school.projectservice.service;

import java.util.List;

import faang.school.projectservice.exception.ProjectNotFoundException;
import org.springframework.stereotype.Service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.ProjectNotFoundException;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ProjectMapper projectMapper;
    private final List<ProjectFilter> projectFilters;
    private final UserContext userContext;

    @Override
    public ProjectDto create(ProjectDto projectDto) {
        if (projectRepository.existsByOwnerIdAndName(userContext.getUserId(), projectDto.getName())) {
            throw new IllegalArgumentException(
                String.format("User {} already has a project with name {}.", userContext.getUserId(), projectDto.getName())
            );
        }

        if (projectDto.getVisibility() == null) {
            projectDto.setVisibility(ProjectVisibility.PRIVATE);
        }
        projectDto.setStatus(ProjectStatus.CREATED);

        Project savedProject = projectRepository.save(projectMapper.toEntity(projectDto));

        return projectMapper.toDto(savedProject);
    }

    @Override
    public ProjectDto update(ProjectDto projectDto) {
        Project project = projectRepository.findById(projectDto.getId())
            .orElseThrow(() -> new EntityNotFoundException(
                String.format("Project {} not found.", projectDto.getId())
            ));

        if (project.getOwnerId() != userContext.getUserId()) {
            throw new IllegalArgumentException(
                String.format("You are not the owner of {} project", projectDto.getId())
            );
        }

        projectMapper.update(project, projectDto);
        project = projectRepository.save(project);

        return projectMapper.toDto(project);
    }

    @Override
    public List<ProjectDto> getAll(ProjectFilterDto projectFilter) {
        List<Project> projects = projectRepository.findAll();
        List<ProjectDto> projectDtos = projectMapper.toDtos(projects);

        for (ProjectFilter filter : projectFilters) {
            if (filter.isApplicable(projectFilter)) {
                projectDtos = filter.apply(projectDtos.stream(), projectFilter).toList();
            }
        }

        projectDtos = filterByPrivacy(projectDtos);

        return projectDtos;
    }

    @Override
    public List<ProjectDto> getAll() {
        List<Project> projects = projectRepository.findAll();
        List<ProjectDto> projectDtos = projectMapper.toDtos(projects);

        projectDtos = filterByPrivacy(projectDtos);

        return projectDtos;
    }

    @Override
    public ProjectDto getById(long projectId) {
        return projectRepository.findById(projectId)
                .map(projectMapper::toDto)
                .orElseThrow(() -> new ProjectNotFoundException("Проект не найден: " + projectId));
    }

    @Override
    public List<ProjectDto> getProjectsByIds(List<Long> ids) {
        return projectRepository.findAllById(ids)
                .stream()
                .map(projectMapper::toDto)
                .toList();
    }

    private List<ProjectDto> filterByPrivacy(List<ProjectDto> projectDtos) {
        return projectDtos.stream()
            .filter(this::privacyFilter)
            .toList();
    }

    private boolean privacyFilter(ProjectDto projectDto) {
        if (projectDto.getVisibility() != ProjectVisibility.PRIVATE) {
            return true;
        }
        boolean isOwner = projectDto.getOwnerId() == userContext.getUserId();
        boolean isTeamMember = userIsTeamMember(projectDto.getId());

        return isOwner || isTeamMember;
    }

    private boolean userIsTeamMember(long projectId) {
        return teamMemberRepository.findByUserIdAndProjectId(userContext.getUserId(), projectId) != null;
    }
}
