package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.service.ProjectService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ProjectControllerTest {
    @Mock
    private ProjectService projectService;
    @InjectMocks
    private ProjectController projectController;
    private final long ownerId = 1L;
    private final String name = "Project";
    private final String description = "Cool project";
    private final long projectId = 10L;
    private ProjectDto projectDto;

    @BeforeEach
    void setUp() {
        projectDto = ProjectDto.builder().id(projectId).name(name).description(description).ownerId(ownerId).build();
    }

    @Test
    public void testCreateInvalidArguments() {
        String empty = "";
        projectDto = ProjectDto.builder().id(projectId).name(name).description(empty).ownerId(ownerId).build();

        Assertions.assertThrows(RuntimeException.class, () -> projectController.create(projectDto));

        projectDto = ProjectDto.builder().id(projectId).name(name).description(null).ownerId(ownerId).build();

        Assertions.assertThrows(RuntimeException.class, () -> projectController.create(projectDto));

        projectDto = ProjectDto.builder().id(projectId).name(empty).description(description).ownerId(ownerId).build();

        Assertions.assertThrows(RuntimeException.class, () -> projectController.create(projectDto));

        projectDto = ProjectDto.builder().id(projectId).name(null).description(description).ownerId(ownerId).build();

        Assertions.assertThrows(RuntimeException.class, () -> projectController.create(projectDto));

        projectDto = ProjectDto.builder().id(projectId).name(empty).description(empty).ownerId(ownerId).build();

        Assertions.assertThrows(RuntimeException.class, () -> projectController.create(projectDto));

        projectDto = ProjectDto.builder().id(projectId).name(null).description(null).ownerId(ownerId).build();

        Assertions.assertThrows(RuntimeException.class, () -> projectController.create(projectDto));
    }

    @Test
    public void testCreate() {
        Mockito.when(projectService.create(projectDto)).thenReturn(projectDto);
        Assertions.assertEquals(projectDto, projectController.create(projectDto));
        Mockito.verify(projectService).create(projectDto);
    }

    @Test
    public void testUpdateInvalidArguments() {
        long id = 0L;
        projectDto.setId(id);
        Assertions.assertThrows(RuntimeException.class, () -> projectController.update(projectDto));

        projectDto.setId(null);
        Assertions.assertThrows(RuntimeException.class, () -> projectController.update(projectDto));
    }

    @Test
    public void testGetProjectsWithFilters() {
        Mockito.when(projectService.getProjectsWithFilters(projectDto)).thenReturn(List.of(projectDto));
        projectController.getProjectsWithFilters(projectDto);
        Mockito.verify(projectService).getProjectsWithFilters(projectDto);
    }

    @Test
    public void testGetAllProjects() {
        Mockito.when(projectService.getAllProjects()).thenReturn(List.of(projectDto));
        projectController.getAllProjects();
        Mockito.verify(projectService).getAllProjects();
    }

    @Test
    public void testGetProjectByIdWithWrongId() {
        long id = 0L;
        projectDto.setId(id);
        Assertions.assertThrows(RuntimeException.class, () -> projectController.getProjectById(projectDto));

        projectDto.setId(null);
        Assertions.assertThrows(RuntimeException.class, () -> projectController.getProjectById(projectDto));
    }

    @Test
    public void testGetProjectById() {
        Mockito.when(projectService.getProjectById(projectDto)).thenReturn(projectDto);
        projectController.getProjectById(projectDto);
        Mockito.verify(projectService).getProjectById(projectDto);
    }
}
