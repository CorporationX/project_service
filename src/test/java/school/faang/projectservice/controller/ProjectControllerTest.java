package school.faang.projectservice.controller;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import faang.school.projectservice.controller.ProjectController;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.service.ProjectService;

@ExtendWith(MockitoExtension.class)
public class ProjectControllerTest {
    @Mock
    ProjectService projectService;

    @InjectMocks
    ProjectController projectController;

    @Test
    public void testCreate_whenValidProjectDto_returnsProjectDto() {
        ProjectDto projectDto = ProjectDto.builder()
            .name("Project Name")
            .description("Description")
            .build();
        when(projectService.create(projectDto)).thenReturn(projectDto);
        assertEquals(projectDto, projectController.create(projectDto));
    }

    @Test
    public void testCreate_whenEmptyName_thenThrowsIllegalArgumentException() {
        ProjectDto projectDto = ProjectDto.builder()
            .name("")
            .description("Description")
            .build();
        assertThrows(IllegalArgumentException.class, () -> projectController.create(projectDto));
    }

    @Test
    public void testCreate_whenNullName_thenThrowsIllegalArgumentException() {
        ProjectDto projectDto = ProjectDto.builder()
            .description("Description")
            .build();
        assertThrows(IllegalArgumentException.class, () -> projectController.create(projectDto));
    }

    @Test
    public void testCreate_whenEmptyDescription_thenThrowsIllegalArgumentException() {
        ProjectDto projectDto = ProjectDto.builder()
            .name("Project Name")
            .description("")
            .build();
        assertThrows(IllegalArgumentException.class, () -> projectController.create(projectDto));
    }

    @Test
    public void testCreate_whenNullDescription_thenThrowsIllegalArgumentException() {
        ProjectDto projectDto = ProjectDto.builder()
            .name("Project Name")
            .build();
        assertThrows(IllegalArgumentException.class, () -> projectController.create(projectDto));
    }

}
