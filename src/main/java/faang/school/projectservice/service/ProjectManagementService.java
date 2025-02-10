package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectReadDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.exception.BusinessException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.NoAccessException;
import faang.school.projectservice.fillters.project.ProjectFilter;
import faang.school.projectservice.mapper.ProjectEntityMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectManagementService {

    private final ProjectRepository projectRepository;
    private final ProjectEntityMapper projectEntityMapper;
    private final List<ProjectFilter> projectFilters;

    public ProjectReadDto createProject(ProjectCreateDto projectCreateDto, long userId) {
        if (projectRepository.existsByOwnerIdAndName(userId, projectCreateDto.getName())) {
            throw new BusinessException("Проект с таким названием уже существует для этого владельца");
        }

        Project project = projectEntityMapper.toEntity(projectCreateDto);
        project.setOwnerId(userId);
        project.setStatus(ProjectStatus.CREATED);
        return projectEntityMapper.toProjectDto(projectRepository.save(project));
    }

    public ProjectReadDto updateProject(ProjectUpdateDto projectUpdateDto, long projectId, long userId) {
        Project project = getProject(projectId);

        if(!isVisibleToUser(project,userId)){
            throw new NoAccessException("У вас нет доступа к проекту с id: " + project.getId());
        }

        projectEntityMapper.updateEntityFromDto(projectUpdateDto, project);
        return projectEntityMapper.toProjectDto(projectRepository.save(project));
    }

    public List<ProjectReadDto> getAllProjects(ProjectFilterDto filters, long userId) {
        Stream<Project> projectStream  = projectRepository.findAll().stream();

        for (ProjectFilter filter : projectFilters) {
                projectStream  = filter.apply(projectStream , filters);
        }

        List<Project> filteredProjects = projectStream.toList();
        return mapProjectsToDtoList(userId, filteredProjects);
    }

    public ProjectReadDto getProjectById(long projectId, long userId){
        Project project = getProject(projectId);

        if(!isVisibleToUser(project,userId)){
            throw new NoAccessException("У вас нет доступа к проекту с id: " + projectId);
        }

        return projectEntityMapper.toProjectDto(project);
    }

    private Project getProject(long projectId) {
        return projectRepository.findById(projectId).orElseThrow(
                () -> new EntityNotFoundException("Не существует проекта с ID: " + projectId));
    }

    private List<ProjectReadDto> mapProjectsToDtoList(long userId, List<Project> projects) {
        return projects.stream()
                .filter(project -> isVisibleToUser(project, userId))
                .map(projectEntityMapper::toProjectDto)
                .toList();
    }

    private boolean isVisibleToUser(Project project, Long userId) {
        if (project.getVisibility() == ProjectVisibility.PRIVATE) {
            if(project.getOwnerId().equals(userId)){
                return true;
            }
            return isParticipant(project, userId);
        }
        return true;
    }

    private boolean isParticipant(Project project, Long userId) {
        return project.getTeams() != null && project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .anyMatch(member -> member.getId().equals(userId));
    }

}
