package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataAccessException;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectJpaRepository projectJpaRepository;

    private final ProjectMapper projectMapper;


    public ProjectDto createProject(ProjectDto projectDto, long requestUserId) {
        if(projectDto.getName().isEmpty() || projectDto.getDescription().isEmpty()){
            throw new IllegalStateException("Projects name and description cannot be null");
        }
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

    public ProjectDto updateProject(long projectId, String description, ProjectStatus status, long requestUserId) {
        Project project = projectJpaRepository.findById( projectId )
                .orElseThrow( () -> new EntityNotFoundException( "Project with id " + projectId + " does not exist" ) );

        checkUserIsMemberOrThrowException( project, requestUserId );

        project.setStatus( status != null ? status : project.getStatus() );
        project.setDescription( description != null ? description : project.getDescription() );

        projectJpaRepository.save( project );
        return projectMapper.toDto( project );
    }

    public List<ProjectDto> findProjectsByName(String name, long requestUserId) {
        List<Project> projectList = projectJpaRepository.findAll();
        if (projectList.isEmpty()) {
            throw new EntityNotFoundException( "No projects found by name: " + name );
        }

        List<Project> filteredProjects = projectList.stream()
                .filter( project -> project.getName().equals( name ) && (project.getVisibility().equals( ProjectVisibility.PUBLIC ) ||
                        (project.getVisibility().equals( ProjectVisibility.PRIVATE ) &&
                                isMember( project, requestUserId ))) )
                .toList();

        if (filteredProjects.isEmpty()) {
            throw new EntityNotFoundException( "No projects found by name: " + name + " and available to user ID:" + requestUserId );
        }
        return projectMapper.toDtoList( filteredProjects );
    }

    public List<ProjectDto> findProjectsByStatus(ProjectStatus status, long requestUserId) {
        List<Project> projectList = projectJpaRepository.findAll();
        if (projectList.isEmpty()) {
            throw new EntityNotFoundException( "No projects found by status: " + status );
        }

        List<Project> filteredProjects = projectList.stream()
                .filter( project -> project.getStatus().equals( status ) && (project.getVisibility().equals( ProjectVisibility.PUBLIC ) ||
                        (project.getVisibility().equals( ProjectVisibility.PRIVATE ) &&
                                isMember( project, requestUserId ))) )
                .toList();

        if (filteredProjects.isEmpty()) {
            throw new EntityNotFoundException( "No projects found by status: " + status + " and available to user ID:" + requestUserId );
        }

        return projectMapper.toDtoList( filteredProjects );
    }

    public List<ProjectDto> getAllProjects(long requestUserId) {
        List<Project> projectList = projectJpaRepository.findAll();
        if (projectList.isEmpty()) {
            throw new EntityNotFoundException( "No projects found" );
        }

        List<Project> filteredProjects = projectList.stream()
                .filter( project -> project.getVisibility().equals( ProjectVisibility.PUBLIC ) ||
                        (project.getVisibility().equals( ProjectVisibility.PRIVATE ) &&
                                isMember( project, requestUserId )) )
                .toList();

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

}
