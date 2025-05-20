package faang.school.projectservice.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
            throw new IllegalArgumentException("You already have a project with this name");
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
            .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        if (project.getOwnerId() != userContext.getUserId()) {
            throw new IllegalArgumentException("You are not the owner of this project");
        }

        projectMapper.update(project, projectDto);
        project.setUpdatedAt(LocalDateTime.now());
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
        Project project = projectRepository.getReferenceById(projectId);
        return projectMapper.toDto(project);
    }

    private List<ProjectDto> filterByPrivacy(List<ProjectDto> projectDtos) {
        return projectDtos.stream()
            .filter(this::privacyFilter)
            .toList();
    }

    private boolean privacyFilter(ProjectDto projectDto) {
        if (projectDto.getVisibility() == ProjectVisibility.PRIVATE) {
            if (projectDto.getOwnerId() == userContext.getUserId() || userIsTeamMember(projectDto.getId())) {
                return true;
            } else { 
                return false;
            }
        } else {
            return true;
        }
    }

    private boolean userIsTeamMember(long projectId) {
        if (teamMemberRepository.findByUserIdAndProjectId(userContext.getUserId(), projectId) != null) {
            return true;
        };
        return false;
    }
}
