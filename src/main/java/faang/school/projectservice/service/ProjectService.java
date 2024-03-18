package faang.school.projectservice.service;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataAccessException;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.service.filter.ProjectFilter;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectJpaRepository projectJpaRepository;
    private final ProjectMapper projectMapper;

    private final List<ProjectFilter> projectFilters;

    public ProjectDto createProject(ProjectDto projectDto, long requestUserId) {
        if (projectJpaRepository.existsByOwnerIdAndName( requestUserId, projectDto.getName() )) {
            throw new IllegalStateException( "User ID " + requestUserId + "is already a member of the team associated with the existing project." );
        }
        Project projectToSave = projectMapper.toEntity( projectDto );
        projectToSave.setStatus( ProjectStatus.CREATED );
        projectToSave.setOwnerId( requestUserId );
        projectToSave.setVisibility( projectDto.getVisibility() == null ? ProjectVisibility.PUBLIC : projectDto.getVisibility() );
        Project savedProject = projectJpaRepository.save( projectToSave );

        return projectMapper.toDto( savedProject );
    }

    public ProjectDto updateProject(long projectId, ProjectDto projectDto, long requestUserId) {
        Project existingProject = projectJpaRepository.findById( projectId )
                .orElseThrow( () -> new EntityNotFoundException( "Project with id " + projectId + " does not exist" ) );

        checkUserIsMemberOrThrowException( existingProject, requestUserId );

        projectMapper.updateEntityFromDto(projectDto, existingProject);

        projectJpaRepository.save( existingProject );

        return projectMapper.toDto( existingProject );
    }

    public List<ProjectDto> findAllProjectsByFilters(ProjectFilterDto filters, long requestUserId) {
        List<Project> projectList = projectJpaRepository.findAll();
        if (projectList.isEmpty()) {
            throw new EntityNotFoundException( "No projects found" );
        }

        List<Project> filteredProjects = filterProjects(projectList,requestUserId);

        Stream<Project> projectStream = filteredProjects.stream();

        if (filteredProjects.isEmpty()) {
            throw new EntityNotFoundException( "No projects found available to user ID:" + requestUserId );
        }
        return projectFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(projectStream, filters))
                .map(projectMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ProjectDto> getAllProjects(long requestUserId) {
        List<Project> projectList = projectJpaRepository.findAll();
        if (projectList.isEmpty()) {
            throw new EntityNotFoundException( "No projects found" );
        }

        List<Project> filteredProjects = filterProjects(projectList, requestUserId);

        return projectMapper.toDtoList( filteredProjects );
    }

    public ProjectDto getProjectById(long projectId, long requestUserId) {
        Project project = projectJpaRepository.findById( projectId )
                .orElseThrow( () -> new EntityNotFoundException( "Project with id " + projectId + " does not exist" ) );

        checkUserIsMemberOrThrowException( project, requestUserId );

        return projectMapper.toDto( project );
    }

    private void checkUserIsMemberOrThrowException(Project project, long requestUserId) {
        if (!isMember( project, requestUserId )) {
            throw new DataAccessException( "User ID " + requestUserId + " is not a member of private project " + project.getId() );
        }

    }

    private boolean isMember(Project project, long userId) {
        return project.getTeams().stream()
                .flatMap( team -> team.getTeamMembers().stream() )
                .anyMatch( teamMember -> teamMember.getUserId() == userId || project.getOwnerId() == userId);
    }

    private List<Project> filterProjects(List<Project> projects, long userId){
        return projects.stream()
              .filter( project -> (project.getVisibility().equals( ProjectVisibility.PUBLIC ) ||
                        (project.getVisibility().equals( ProjectVisibility.PRIVATE ) &&
                                isMember( project, userId ))) )
              .toList();
    }

}
