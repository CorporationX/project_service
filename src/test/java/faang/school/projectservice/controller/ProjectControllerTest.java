package faang.school.projectservice.controller;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.service.ProjectServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectControllerTest {
    @InjectMocks
    ProjectController projectController;
    @Mock
    private ProjectServiceImpl projectService;
    ProjectDto projectDto;
    ProjectFilterDto projectFilterDto;

    @BeforeEach
    void init() {
        projectDto = ProjectDto.builder().id(1L).name("first project").build();
        projectFilterDto = new ProjectFilterDto();
    }

    @Test
    public void testCreateWithNPE() {
        var exception = assertThrows(NullPointerException.class, () -> projectController.create(null));
        assertEquals(exception.getMessage(), "projectDto is marked non-null but is null");
    }

    @Test
    public void testUpdateWithNPE() {
        var exception = assertThrows(NullPointerException.class, () -> projectController.update(null));
        assertEquals(exception.getMessage(), "projectDto is marked non-null but is null");
    }

    @Test
    public void testGetAllByFilterWithNPE() {
        var exception = assertThrows(NullPointerException.class, () -> projectController.getAllByFilter(null));
        assertEquals(exception.getMessage(), "projectFilterDto is marked non-null but is null");
    }

    @Test
    public void testCreateWithCreating() {
        when(projectService.create(projectDto)).thenReturn(projectDto);
        ProjectDto result = projectController.create(projectDto);
        verify(projectService, times(1)).create(projectDto);
        assertEquals(result, projectDto);
        assertEquals(result.getId(), projectDto.getId());
        assertEquals(result.getName(), projectDto.getName());
    }

    @Test
    public void testUpdateWithUpdating() {
        when(projectService.update(projectDto)).thenReturn(projectDto);
        ProjectDto result = projectController.update(projectDto);
        verify(projectService, times(1)).update(projectDto);
        assertEquals(result, projectDto);
        assertEquals(result.getId(), projectDto.getId());
        assertEquals(result.getName(), projectDto.getName());
    }

    @Test
    public void testGetAllWithGetting() {
        when(projectService.getAll()).thenReturn(List.of(projectDto));
        List<ProjectDto> result = projectController.getAll();
        verify(projectService, times(1)).getAll();
        assertEquals(result.get(0), projectDto);
        assertEquals(result.get(0).getId(), projectDto.getId());
        assertEquals(result.get(0).getName(), projectDto.getName());
    }

    @Test
    public void testFindByIdWithFinding() {
        when(projectService.findById(projectDto.getId())).thenReturn(projectDto);
        ProjectDto result = projectController.findById(projectDto.getId());
        verify(projectService, times(1)).findById(projectDto.getId());
        assertEquals(result, projectDto);
        assertEquals(result.getId(), projectDto.getId());
        assertEquals(result.getName(), projectDto.getName());
    }

    @Test
    public void testGetAllByFilterWithGetting() {
        projectController.getAllByFilter(projectFilterDto);
        List<ProjectDto> projectDtos = verify(projectService, times(1)).getAllByFilter(projectFilterDto);
        assertNull(projectDtos);
    }
}
