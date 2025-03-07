package faang.school.projectservice.controller;

import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectResponseDto;
import faang.school.projectservice.dto.subproject.UpdateSubProjectDto;
import faang.school.projectservice.mapper.SubProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.impl.SubProjectServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectService projectService;

    @Spy
    private SubProjectMapperImpl subProjectMapper;

    @InjectMocks
    private SubProjectServiceImpl subProjectService;


    private Project parentProject;
    private Project subProject;
    private CreateSubProjectDto createSubProjectDto;
    private UpdateSubProjectDto updateSubProjectDto;

    @BeforeEach
    void setUp() {
        parentProject = new Project();
        parentProject.setId(1L);
        parentProject.setVisibility(ProjectVisibility.PUBLIC);

        subProject = new Project();
        subProject.setId(1L);
        subProject.setVisibility(ProjectVisibility.PUBLIC);
        subProject.setStatus(ProjectStatus.CREATED);

        createSubProjectDto = CreateSubProjectDto.builder()
                .parentId(1L)
                .visibility(ProjectVisibility.PUBLIC)
                .name("SubProject")
                .description("Description")
                .status(ProjectStatus.CREATED)
                .build();

        updateSubProjectDto = UpdateSubProjectDto.builder()
                .subProjectDtos(Collections.emptyList())
                .visibility(ProjectVisibility.PUBLIC)
                .build();
    }

    @Test
    void testCreateSubProject_Success() {
        Project parentProject = new Project();
        parentProject.setId(1L);
        parentProject.setVisibility(ProjectVisibility.PUBLIC);

        when(projectService.getProject(1L)).thenReturn(parentProject);
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SubProjectResponseDto result = subProjectService.createSubProject(createSubProjectDto);

        assertNotNull(result);
        assertEquals("SubProject", result.name());
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void testCreateSubProject_ParentNotFound() {
        when(projectService.getProject(1L)).thenThrow(new IllegalArgumentException("Parent project not found"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                subProjectService.createSubProject(createSubProjectDto));

        assertEquals("Parent project not found", exception.getMessage());
    }

    @Test
    void testCreateSubProject_ParentIsPrivate() {
        parentProject.setVisibility(ProjectVisibility.PRIVATE);
        when(projectService.getProject(1L)).thenReturn(parentProject);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                subProjectService.createSubProject(createSubProjectDto));

        assertEquals("Parent project is private", exception.getMessage());
    }

    @Test
    void testUpdateSubProject_Success() {
        when(projectService.getProject(1L)).thenReturn(subProject);

        SubProjectResponseDto result = subProjectService.updateSubProject(1L, updateSubProjectDto);

        assertNotNull(result);
        assertEquals(1L, result.id());
    }

    @Test
    void testUpdateSubProject_NotFound() {
        when(projectService.getProject(1L)).thenThrow(new IllegalArgumentException("No project found to update"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                subProjectService.updateSubProject(1L, updateSubProjectDto));

        assertEquals("No project found to update", exception.getMessage());
    }
}