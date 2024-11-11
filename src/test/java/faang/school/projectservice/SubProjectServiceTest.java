package faang.school.projectservice;

import faang.school.projectservice.dto.subprojectDto.subprojectDto.CreateSubProjectDto;
import faang.school.projectservice.dto.subprojectDto.subprojectDto.ProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.SubProjectService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubProjectServiceTest {

    @InjectMocks
    private SubProjectService projectService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    private Project parentProject;
    private CreateSubProjectDto createSubProjectDto;
    private Project subProject;
    private ProjectDto expectedProjectDto;


    @BeforeEach
    void setUp() {
        parentProject = Project.builder()
                .id(1L)
                .name("Parent Project")
                .description("Description of Parent Project")
                .build();

        createSubProjectDto = new CreateSubProjectDto();
        createSubProjectDto.setName("Subproject 1");
        createSubProjectDto.setDescription("Description of Subproject");

        expectedProjectDto = new ProjectDto();
        expectedProjectDto.setId(2L);
        expectedProjectDto.setName("Subproject 1");
        expectedProjectDto.setDescription("Description of Subproject");
        expectedProjectDto.setOwnerId(1L);

        subProject = Project.builder()
                .id(2L)
                .name("Subproject 1")
                .description("Description of Subproject")
                .parentProject(parentProject)
                .build();
    }

    @Test
    void createSubProject() {
        when(projectRepository.getProjectById(1L)).thenReturn(parentProject);

        when(projectRepository.save(any(Project.class))).thenReturn(subProject);

        when(projectMapper.toEntity(createSubProjectDto)).thenReturn(subProject);
        when(projectMapper.toDto(subProject)).thenReturn(expectedProjectDto);

        ProjectDto createdSubProject = projectService.createSubProject(1L, createSubProjectDto);

        verify(projectRepository).getProjectById(1L);
        verify(projectRepository).save(any(Project.class));
        verify(projectMapper).toEntity(createSubProjectDto);
        verify(projectMapper).toDto(subProject);

        assertNotNull(createdSubProject);
        assertEquals(expectedProjectDto.getId(), createdSubProject.getId());
        assertEquals(expectedProjectDto.getName(), createdSubProject.getName());
        assertEquals(expectedProjectDto.getDescription(), createdSubProject.getDescription());
        assertEquals(expectedProjectDto.getOwnerId(), createdSubProject.getOwnerId());
    }

    @Test
    void testUpdateSubProject_EntityNotFoundException() {
        when(projectRepository.getProjectById(1L)).thenReturn(null);

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            projectService.updateSubProject(1L, createSubProjectDto);
        });

        assertEquals("Subproject with ID 1 not found", exception.getMessage());
    }

    @Test
    void testUpdateSubProject_ParentProjectCancelled() {
        parentProject.setStatus(ProjectStatus.CANCELLED);
        when(projectRepository.getProjectById(1L)).thenReturn(subProject);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            projectService.updateSubProject(1L, createSubProjectDto);
        });

        assertEquals("Cannot update subproject because the parent project is already closed.", exception.getMessage());
    }
    @Test
    void testUpdateSubProject_ParentProjectCompletedWithOpenSubProjects() {
        parentProject.setStatus(ProjectStatus.COMPLETED);
        Project openSubProject = new Project();
        openSubProject.setStatus(ProjectStatus.CREATED);
        parentProject.setChildren(Arrays.asList(openSubProject));

        when(projectRepository.getProjectById(1L)).thenReturn(subProject);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            projectService.updateSubProject(1L, createSubProjectDto);
        });

        assertEquals("Cannot close parent project because there are open subprojects.", exception.getMessage());
    }

    @Test
    void testUpdateSubProject_Success() {
        parentProject.setStatus(ProjectStatus.IN_PROGRESS);
        when(projectRepository.getProjectById(1L)).thenReturn(subProject);
        when(projectMapper.toDto(any(Project.class))).thenReturn(new ProjectDto());

        Project updatedSubProject = new Project();
        updatedSubProject.setName(createSubProjectDto.getName());
        updatedSubProject.setDescription(createSubProjectDto.getDescription());
        when(projectRepository.save(any(Project.class))).thenReturn(updatedSubProject);

        ProjectDto result = projectService.updateSubProject(1L, createSubProjectDto);

        assertNotNull(result);
        assertEquals(createSubProjectDto.getName(), updatedSubProject.getName());
        assertEquals(createSubProjectDto.getDescription(), updatedSubProject.getDescription());
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void testUpdateSubProject_ParentProjectPrivate() {
        parentProject.setVisibility(ProjectVisibility.PRIVATE);
        when(projectRepository.getProjectById(1L)).thenReturn(subProject);
        when(projectMapper.toDto(any(Project.class))).thenReturn(new ProjectDto());

        Project updatedSubProject = new Project();
        updatedSubProject.setVisibility(ProjectVisibility.PRIVATE);
        when(projectRepository.save(any(Project.class))).thenReturn(updatedSubProject);

        projectService.updateSubProject(1L, createSubProjectDto);

        assertEquals(ProjectVisibility.PRIVATE, updatedSubProject.getVisibility());
    }

    @Test
    void testUpdateSubProject_SaveError() {
        when(projectRepository.getProjectById(1L)).thenReturn(subProject);
        when(projectRepository.save(any(Project.class))).thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            projectService.updateSubProject(1L, createSubProjectDto);
        });

        assertEquals("Failed to update subproject", exception.getMessage());
    }

    @Test
    void testGetSubProject_ParentProjectNotFound() {
        Long parentProjectId = 1L;
        Long subProjectId = 2L;

        when(projectRepository.getProjectById(parentProjectId)).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> projectService.getSubProject(parentProjectId, subProjectId));
        assertEquals("Parent project with ID 1 not found.", exception.getMessage());
    }

    @Test
    void testGetSubProject_NoSubProjects() {
        // Arrange
        Long parentProjectId = 1L;
        Long subProjectId = 2L;

        parentProject.setChildren(new ArrayList<>());

        when(projectRepository.getProjectById(parentProjectId)).thenReturn(parentProject);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> projectService.getSubProject(parentProjectId, subProjectId));
        assertEquals("Parent project with ID 1 has no subprojects.", exception.getMessage());
    }

    @Test
    void testGetSubProject_SubProjectPrivate() {
        Long parentProjectId = 1L;
        Long subProjectId = 2L;

        Project subProject = Project.builder()
                .id(subProjectId)
                .name("Private Subproject")
                .visibility(ProjectVisibility.PRIVATE)
                .build();

        parentProject.setChildren(Arrays.asList(subProject));

        when(projectRepository.getProjectById(parentProjectId)).thenReturn(parentProject);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> projectService.getSubProject(parentProjectId, subProjectId));

        assertEquals("Cannot find subproject because it is private.", exception.getMessage());
    }

    @Test
    void testGetSubProject_SubProjectNotFound() {
        Long parentProjectId = 1L;
        Long subProjectId = 2L;

        Project anotherSubProject = Project.builder()
                .id(3L)
                .name("Another Subproject")
                .build();

        parentProject.setChildren(Arrays.asList(anotherSubProject));

        when(projectRepository.getProjectById(parentProjectId)).thenReturn(parentProject);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> projectService.getSubProject(parentProjectId, subProjectId));

        assertEquals("Subproject with ID 2 not found", exception.getMessage());
    }

    @Test
    void testGetSubProject_ParentHasNoSubProjects() {
        Long parentProjectId = 1L;
        Long subProjectId = 2L;

        parentProject.setChildren(new ArrayList<>());

        when(projectRepository.getProjectById(parentProjectId)).thenReturn(parentProject);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> projectService.getSubProject(parentProjectId, subProjectId));

        assertEquals("Parent project with ID 1 has no subprojects.", exception.getMessage());
    }

    @Test
    void testGetSubProject_Success() {
        Long parentProjectId = 1L;
        Long subProjectId = 2L;

        Project subProject = Project.builder()
                .id(subProjectId)
                .name("Subproject 1")
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        parentProject.setChildren(Arrays.asList(subProject));

        when(projectRepository.getProjectById(parentProjectId)).thenReturn(parentProject);
        when(projectMapper.toDto(subProject)).thenReturn(expectedProjectDto);

        ProjectDto result = projectService.getSubProject(parentProjectId, subProjectId);

        assertNotNull(result);
        assertEquals(expectedProjectDto.getId(), result.getId());
        assertEquals(expectedProjectDto.getName(), result.getName());
        assertEquals(expectedProjectDto.getDescription(), result.getDescription());
    }
}
