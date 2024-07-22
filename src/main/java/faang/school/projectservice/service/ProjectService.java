package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.filter.ProjectFilter;
import faang.school.projectservice.service.updater.ProjectUpdater;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final List<ProjectFilter> projectFilters;
    private final List<ProjectUpdater> projectUpdaters;
    private final UserContext userContext;

    public ProjectDto create(ProjectDto projectDto) {
        if (projectDto.getOwnerId() == null) {
            projectDto.setOwnerId(userContext.getUserId());
        }
        if (projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())) {
            throw new RuntimeException("Project " + projectDto.getName() + " already created by " + projectDto.getOwnerId());
        }
        projectDto.setStatus(ProjectStatus.CREATED);
        return projectMapper.toDto(projectRepository.save(projectMapper.toEntity(projectDto)));
    }

    public ProjectDto update(ProjectDto projectDto) {
        try {
            Project project = projectRepository.getProjectById(projectDto.getId());
            projectUpdaters.stream().filter(filter -> filter.isApplicable(projectDto))
                    .forEach(filter -> filter.apply(project, projectDto));
            return projectMapper.toDto(projectRepository.save(project));
        } catch (EntityNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public List<ProjectDto> getProjectsWithFilters(ProjectDto projectDto) {
        ProjectFilterDto filters = projectMapper.toFilter(projectDto);
        long userId = userContext.getUserId();
        Stream<Project> publicProjects = projectRepository.findAll().stream()
                .filter(project -> project.getVisibility() == ProjectVisibility.PUBLIC);
        Stream<Project> privateProjects = projectRepository.findAll().stream()
                .filter(project -> project.getVisibility() == ProjectVisibility.PRIVATE)
                .filter(project -> project.getTeams().stream()
                        .anyMatch(team -> team.getTeamMembers().stream()
                                .anyMatch(member -> member.getId().equals(userId))));
        Stream<Project> resultStream = Stream.concat(publicProjects, privateProjects);
        return projectFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(resultStream, filters))
                .map(projectMapper::toDto)
                .toList();
    }

    public List<ProjectDto> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(projectMapper::toDto)
                .toList();
    }

    public ProjectDto getProjectById(ProjectDto projectDto) {
        try {
            return projectMapper.toDto(projectRepository.getProjectById(projectDto.getId()));
        } catch (EntityNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
