package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataAccessException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    private final ProjectMapper projectMapper;

    public ProjectDto getProjectById(long projectId, long requestUserId){
        Project project = projectRepository.findById( projectId )
                .orElseThrow(() -> new EntityNotFoundException("Project with id " + projectId + " does not exist") );
        boolean isAccessible = false;
        if(project.getVisibility().equals( ProjectVisibility.PRIVATE ))
            isAccessible = projectRepository.isUserIsAFollower(projectId, requestUserId);

        if(isAccessible) return projectMapper.toDto( project );
        else throw new DataAccessException( "This project is private" );
    }

    public List<ProjectDto> getAllProjects(long requestUserId){
        List<Project> projectPublicList = projectRepository.findProjectsAccessibleToUser(ProjectVisibility.PUBLIC);
        if (projectList.isEmpty()) {
            throw new EntityNotFoundException("No projects found ");
        }
        return projectMapper.toDtoList( projectList );
    }

    public List<ProjectDto> findProjectsByName(String name) {
        List<Project> projectList = projectRepository.findByName(name);
        if (projectList.isEmpty()) {
            throw new EntityNotFoundException("No projects found by name: " + name);
        }
        return projectMapper.toDtoList(projectList);
    }

    public List<ProjectDto> findProjectsByStatus(ProjectStatus status) {
        List<Project> projectList = projectRepository.findByStatus(status);
        if (projectList.isEmpty()) {
            throw new EntityNotFoundException("No projects found by status: " + status);
        }
        return projectMapper.toDtoList(projectList);
    }

    public ProjectDto updateProject(long projectId, String description, ProjectStatus status){
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project with id " + projectId + " does not exist") );
        if(description == null ) project.getDescription();
        if(status == null) project.getStatus();
        LocalDateTime updatedAt = LocalDateTime.now();
        projectRepository.updateDescriptionAndStatusAndUpdatedAtById( description, status,updatedAt, projectId );

    }

}
