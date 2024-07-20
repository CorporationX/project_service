package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    @Mock
    private  ProjectRepository projectRepository;
    @Mock
    private  ProjectMapper projectMapper;
    @InjectMocks
    private  ProjectService projectService;
    @Test
    public void createAlreadyExists(){
        long ownerId = 1L;
        String name = "Name";
        String description = "";
        Mockito.when(projectRepository.existsByOwnerUserIdAndName(ownerId, name)).thenReturn(true);

        Assertions.assertThrows(RuntimeException.class,() -> projectService.create(ownerId,name,description));
    }
    @Test
    public void create(){
        long ownerId = 1L;
        long id = 0L;
        String name = "Name";
        String description = "";
        Mockito.when(projectRepository.existsByOwnerUserIdAndName(ownerId, name)).thenReturn(false);

        Project project = Project.builder()
                .ownerId(ownerId)
                .name(name)
                .description(description)
                .status(ProjectStatus.CREATED)
                .build();
        ProjectDto projectDto = new ProjectDto(id,name,description,ownerId);
        Mockito.when(projectRepository.save(project)).thenReturn(project);

        Mockito.when(projectMapper.toDto(project)).thenReturn(projectDto);
        Assertions.assertEquals(projectService.create(ownerId,name,description),projectDto);
        Mockito.verify(projectRepository).save(project);
    }
    @Test
    public void updateProjectNotExist(){
        long id = 0L;
        String description = "";
        ProjectStatus status = ProjectStatus.CREATED;
        Mockito.when(projectRepository.getProjectById(id)).thenThrow(EntityNotFoundException.class);
        Assertions.assertThrows(RuntimeException.class,() ->projectService.update(id,description));
        Assertions.assertThrows(RuntimeException.class,() ->projectService.update(id,status));
        Assertions.assertThrows(RuntimeException.class,() ->projectService.update(id,status,description));
    }

    public void update(){
        long ownerId = 1L;
        long id = 0L;
        String name = "Name";
        String sourceDescription = "";
        String resultDescription = "";
        ProjectStatus status = ProjectStatus.CREATED;
        Project project = Project.builder()
                .ownerId(ownerId)
                .name(name)
                .description(sourceDescription)
                .status(ProjectStatus.CREATED)
                .build();
        Mockito.when(projectRepository.getProjectById(id)).thenReturn(project);
        projectService.update(id,resultDescription)

    }
}
