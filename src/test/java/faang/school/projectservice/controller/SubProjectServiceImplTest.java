package faang.school.projectservice.controller;

import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.moment.*;
import faang.school.projectservice.dto.client.StageDto;
import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.dto.subproject.UpdateSubProjectDto;
import faang.school.projectservice.mapper.SubProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.impl.SubProjectServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
    private SubProjectMapper subProjectMapper;

    @Mock
    private MomentRepository momentRepository;

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
        subProject.setId(2L);
        subProject.setVisibility(ProjectVisibility.PUBLIC);

        MomentCreateRequestDto momentDto = MomentCreateRequestDto.builder()
                .date(LocalDateTime.now())
                .build();


        createSubProjectDto = CreateSubProjectDto.builder()
                .parentId(1L)
                .id(2L)
                .subProjectIds(Collections.emptyList())
                .build();

        updateSubProjectDto = UpdateSubProjectDto.builder()
                .id(2L)
                .subProjectIds(Collections.emptyList())
                .stageDto(new StageDto(200L, "Development"))
                .visibility(ProjectVisibility.PUBLIC)
                .build();
    }

    @Test
    void testCreateSubProject_Success() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(parentProject));
        when(projectRepository.save(any(Project.class))).thenReturn(subProject);

        SubProjectDto result = subProjectService.createSubProject(createSubProjectDto);

        assertNotNull(result);
        assertEquals(2L, result.id());
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void testCreateSubProject_ParentNotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                subProjectService.createSubProject(createSubProjectDto));

        assertEquals("Parent project not found", exception.getMessage());
    }

    @Test
    void testCreateSubProject_ParentIsPrivate() {
        parentProject.setVisibility(ProjectVisibility.PRIVATE);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(parentProject));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                subProjectService.createSubProject(createSubProjectDto));

        assertEquals("Parent project is private", exception.getMessage());
    }

    @Test
    void testUpdateSubProject_Success() {
        when(projectRepository.findById(2L)).thenReturn(Optional.of(subProject));

        SubProjectDto result = subProjectService.updateSubProject(updateSubProjectDto);

        assertNotNull(result);
        assertEquals(2L, result.id());
    }

    @Test
    void testUpdateSubProject_NotFound() {
        when(projectRepository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                subProjectService.updateSubProject(updateSubProjectDto));

        assertEquals("No project found to update", exception.getMessage());
    }

    @Test
    void testGetSubProjects_Success() {
        parentProject.setChildren(Collections.singletonList(subProject));

        List<SubProjectDto> result = subProjectService.getSubprojects(parentProject, "Test", ProjectStatus.CREATED);

        assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }
}
