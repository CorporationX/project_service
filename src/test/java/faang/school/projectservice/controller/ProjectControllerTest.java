package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.service.ProjectService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProjectControllerTest {
    @Mock
    private ProjectService projectService;
    @InjectMocks
    private ProjectController projectController;
    private long ownerId = 1L;
    private String name = "Project";
    private String description = "Cool project";

    @Test
    public void createInvalidArguments() {
        String empty = "";
        Assertions.assertThrows(RuntimeException.class, () -> projectController.create(ownerId, name, empty));
        Assertions.assertThrows(RuntimeException.class, () -> projectController.create(ownerId, name, null));
        Assertions.assertThrows(RuntimeException.class, () -> projectController.create(ownerId, empty, description));
        Assertions.assertThrows(RuntimeException.class, () -> projectController.create(ownerId, null, description));
        Assertions.assertThrows(RuntimeException.class, () -> projectController.create(ownerId, empty, empty));
        Assertions.assertThrows(RuntimeException.class, () -> projectController.create(ownerId, null, null));
    }

    @Test
    public void create() {
        ProjectDto projectDto = ProjectDto.builder().name(name).description(description).build();
        Mockito.when(projectService.create(ownerId,name,description)).thenReturn(projectDto);
        Assertions.assertEquals(projectDto, projectController.create(ownerId,name,description));
        Mockito.verify(projectService).create(ownerId,name,description);
    }
@Test
    public void updateInvalidArguments(){

    }
}
