package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectControllerTest {
    @InjectMocks
    ProjectController projectController;
    @Mock
    private ProjectService projectService;

    @Test
    public void testCreateWithNPE() {
        var exception = assertThrows(NullPointerException.class, () -> projectController.create(null));
        assertEquals(exception.getMessage(), "projectDto is marked non-null but is null");
    }

    @Test
    public void testCreateWithCreating() {
        ProjectDto projectDto = ProjectDto.builder().id(1L).name("first project").build();
        when(projectService.create(projectDto)).thenReturn(projectDto);
        ProjectDto result = projectController.create(projectDto);
        verify(projectService, times(1)).create(projectDto);
        assertEquals(result, projectDto);
        assertEquals(result.getId(), projectDto.getId());
        assertEquals(result.getName(), projectDto.getName());
    }
}
