package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.validator.ProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectControllerTest {
    @Mock
    private ProjectService projectService;
    @Mock
    private ProjectValidator projectValidator;
    @InjectMocks
    private ProjectController projectController;
    private ProjectDto projectDto;
    private List<ProjectDto> projectDtos;

    @BeforeEach
    void init() {
        projectDto = ProjectDto.builder()
                .id(1L)
                .ownerId(1L).build();

        projectDtos = new ArrayList<>(List.of(projectDto));
    }

    @Test
    void testCreateProject() {
        when(projectService.create(projectDto)).thenReturn(projectDto);

        ProjectDto projectDtoCreateProject = projectController.create(projectDto);

        verify(projectService, times(1)).create(projectDto);
        assertEquals(projectDto, projectDtoCreateProject);
    }

    @Test
    void testUpdateProject() {
        when(projectService.update(projectDto)).thenReturn(projectDto);

        ProjectDto projectDtoUpdateProject = projectController.update(projectDto);

        verify(projectService, times(1)).update(projectDto);
        assertEquals(projectDto, projectDtoUpdateProject);
    }

    @Test
    void testGetAll() {
        when(projectService.getAll()).thenReturn(projectDtos);

        List<ProjectDto> projectDtoGetALl = projectController.getAll();

        verify(projectService, times(1)).getAll();
        assertEquals(projectDtos, projectDtoGetALl);
    }

    @Test
    void testGetById() {
        long projectId = projectDto.getId();
        when(projectService.getProjectDtoById(projectId)).thenReturn(projectDto);

        ProjectDto projectDtoById = projectController.getById(projectId);

        verify(projectService, times(1)).getProjectDtoById(projectId);
        assertEquals(projectDto, projectDtoById);
    }

    @Test
    void testGetByFilters() {
        ProjectFilterDto projectFilterDto = new ProjectFilterDto();
        when(projectService.getAll(projectFilterDto)).thenReturn(projectDtos);

        List<ProjectDto> ProjectDtosByFilters = projectController.getByFilters(projectFilterDto);

        verify(projectService, times(1)).getAll(projectFilterDto);
        assertEquals(projectDtos, ProjectDtosByFilters);
    }

    @Test
    void testExistsProjectById_exists_returnsTrue() {
        Long projectId = projectDto.getId();
        when(projectService.existsProjectById(projectId)).thenReturn(true);

        boolean isExistsProject = projectController.existsProjectById(projectId);

        verify(projectService, times(1)).existsProjectById(projectId);
        assertTrue(isExistsProject);
    }

    @Test
    void testExistsProjectById_notExists_returnsFalse() {
        Long projectId = projectDto.getId();
        when(projectService.existsProjectById(projectId)).thenReturn(false);

        boolean isExistsProject = projectController.existsProjectById(projectId);

        verify(projectService, times(1)).existsProjectById(projectId);
        assertFalse(isExistsProject);
    }

    @Test
    void successGetAllSubprojectsByFilter() {
        List<ProjectDto> projectDtos = List.of(ProjectDto.builder()
                .id(1L)
                .ownerId(1L)
                .build());
        long parentId = 1L;
        ProjectFilterDto filterDto = new ProjectFilterDto();
        filterDto.setName("Filter");

        when(projectService.getAllSubprojectsByFilter(parentId, filterDto)).thenReturn(projectDtos);
        List<ProjectDto> projectDtosActual = projectController.getAllSubprojectsByFilter(parentId, filterDto);

        verify(projectService).getAllSubprojectsByFilter(parentId, filterDto);
        assertEquals(projectDtos, projectDtosActual);
    }
}
