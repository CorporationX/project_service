package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.dto.subproject.UpdateSubProjectDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.mapper.SubProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubProjectServiceImplTest {
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private MomentRepository momentRepository;
    @Spy
    private SubProjectMapper subProjectMapper;
    @InjectMocks
    private SubProjectServiceImpl subProjectService;

    @Test
    void createSubProject_whenParentProjectNotExist_shouldThrow() {
        CreateSubProjectDto createSubProjectDto = new CreateSubProjectDto(
                1L,
                "Name",
                "Description",
                ProjectVisibility.PUBLIC,
                ProjectStatus.CREATED
        );

        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> subProjectService.createSubProject(createSubProjectDto)
        );

        verify(projectRepository, never()).save(any());
    }

    @Test
    void createSubProject_whenParentProjectISPrivate_shouldThrow() {
        CreateSubProjectDto createSubProjectDto = new CreateSubProjectDto(
                1L,
                "Name",
                "Description",
                ProjectVisibility.PUBLIC,
                ProjectStatus.CREATED
        );

        Project parentProject = new Project();
        parentProject.setId(1L);
        parentProject.setVisibility(ProjectVisibility.PRIVATE);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(parentProject));

        assertThrows(
                IllegalArgumentException.class,
                () -> subProjectService.createSubProject(createSubProjectDto)
        );

        verify(projectRepository, never()).save(any());
    }

    @Test
    void createSubProject_whenParentProjectExistsAndVisibilityOk_shouldSaveSuccessfully() {
        CreateSubProjectDto createSubProjectDto = new CreateSubProjectDto(
                1L,
                "SubProject",
                "SubDescription",
                ProjectVisibility.PUBLIC,
                ProjectStatus.CREATED
        );

        Project parentProject = new Project();
        parentProject.setId(1L);
        parentProject.setName("ParentProject");
        parentProject.setDescription("Parent description");
        parentProject.setVisibility(ProjectVisibility.PUBLIC);

        Project subProjectToSave = new Project();
        subProjectToSave.setName(createSubProjectDto.name());
        subProjectToSave.setDescription(createSubProjectDto.description());
        subProjectToSave.setVisibility(createSubProjectDto.visibility());
        subProjectToSave.setStatus(createSubProjectDto.status());

        Project savedSubProject = new Project();
        savedSubProject.setId(2L);
        savedSubProject.setName(createSubProjectDto.name());
        savedSubProject.setDescription(createSubProjectDto.description());
        savedSubProject.setParentProject(parentProject);
        savedSubProject.setVisibility(createSubProjectDto.visibility());
        savedSubProject.setStatus(createSubProjectDto.status());

        when(projectRepository.findById(1L)).thenReturn(Optional.of(parentProject));
        when(subProjectMapper.toSubProject(createSubProjectDto)).thenReturn(subProjectToSave);
        when(projectRepository.save(subProjectToSave)).thenReturn(savedSubProject);
        when(subProjectMapper.toSubProjectDto(savedSubProject)).thenReturn(
                new SubProjectDto(
                        savedSubProject.getId(),
                        savedSubProject.getName(),
                        savedSubProject.getDescription(),
                        null, null, null,
                        parentProject.getId(),
                        null, null,
                        savedSubProject.getStatus(),
                        savedSubProject.getVisibility(),
                        null, null, null,
                        null
                )
        );

        SubProjectDto result = subProjectService.createSubProject(createSubProjectDto);

        verify(projectRepository).save(subProjectToSave);
        assertEquals(2L, result.id());
        assertEquals("SubProject", result.name());
        assertEquals(parentProject.getId(), result.parentProjectId());
    }

    @Test
    void updateSubProject_whenProjectNotFound_shouldThrow() {
        UpdateSubProjectDto updateDto = new UpdateSubProjectDto(
                "New Name",
                "New Description",
                ProjectStatus.IN_PROGRESS,
                ProjectVisibility.PUBLIC
        );

        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> subProjectService.updateSubProject(updateDto, 1L)
        );

        verify(projectRepository, never()).save(any());
    }

    @Test
    void updateSubProject_whenMakingPrivate_shouldUpdateVisibilityOfAllChildren() {
        Project parent = new Project();
        parent.setId(1L);
        parent.setName("Parent");
        parent.setVisibility(ProjectVisibility.PUBLIC);

        Project child = new Project();
        child.setId(2L);
        child.setVisibility(ProjectVisibility.PUBLIC);
        parent.setChildren(java.util.List.of(child));

        UpdateSubProjectDto updateDto = new UpdateSubProjectDto(
                "Updated Name",
                "Updated Desc",
                ProjectStatus.IN_PROGRESS,
                ProjectVisibility.PRIVATE
        );

        when(projectRepository.findById(1L)).thenReturn(Optional.of(parent));
        when(projectRepository.save(parent)).thenReturn(parent);
        when(subProjectMapper.toSubProjectDto(any(Project.class))).thenAnswer(invocation -> {
            Project p = invocation.getArgument(0);
            return new SubProjectDto(
                    p.getId(),
                    p.getName(),
                    p.getDescription(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    p.getStatus(),
                    p.getVisibility(),
                    null,
                    null,
                    null,
                    null
            );
        });

        SubProjectDto result = subProjectService.updateSubProject(updateDto, 1L);

        verify(projectRepository).save(parent);
        assertEquals(ProjectVisibility.PRIVATE, parent.getVisibility());
        assertEquals(ProjectVisibility.PRIVATE, child.getVisibility());
        assertEquals("Updated Name", result.name());
    }

    @Test
    void updateSubProject_whenCompletingWithIncompleteChildren_shouldThrow() {
        Project parent = new Project();
        parent.setId(1L);
        parent.setStatus(ProjectStatus.IN_PROGRESS);

        Project child = new Project();
        child.setId(2L);
        child.setStatus(ProjectStatus.IN_PROGRESS);

        parent.setChildren(java.util.List.of(child));

        UpdateSubProjectDto updateDto = new UpdateSubProjectDto(
                "Name",
                null,
                ProjectStatus.COMPLETED,
                ProjectVisibility.PUBLIC
        );

        when(projectRepository.findById(1L)).thenReturn(Optional.of(parent));

        assertThrows(
                IllegalStateException.class,
                () -> subProjectService.updateSubProject(updateDto, 1L)
        );

        verify(projectRepository, never()).save(parent);
    }


    @Test
    void updateSubProject_whenValidCompletion_shouldCreateMoment() {
        Project parent = new Project();
        parent.setId(1L);
        parent.setStatus(ProjectStatus.IN_PROGRESS);

        Project child = new Project();
        child.setId(2L);
        child.setStatus(ProjectStatus.COMPLETED);

        Team dummyTeam = new Team();
        dummyTeam.setTeamMembers(new ArrayList<>());
        child.setTeams(List.of(dummyTeam));

        parent.setChildren(List.of(child));

        UpdateSubProjectDto updateDto = new UpdateSubProjectDto(
                "Name",
                null,
                ProjectStatus.COMPLETED,
                ProjectVisibility.PUBLIC
        );

        when(projectRepository.findById(1L)).thenReturn(Optional.of(parent));
        when(projectRepository.save(parent)).thenReturn(parent);

        subProjectService.updateSubProject(updateDto, 1L);

        verify(momentRepository).save(any());
        assertEquals(ProjectStatus.COMPLETED, parent.getStatus());
    }

    @Test
    void getSubProjects_whenProjectNotFound_shouldThrow() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> subProjectService.getSubProjects(1L, null, null));
    }

    @Test
    void getSubProjects_whenNoChildren_shouldReturnEmptyList() {
        Project parent = new Project();
        parent.setId(1L);
        parent.setChildren(null);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(parent));

        List<SubProjectDto> result = subProjectService.getSubProjects(1L, null, null);

        assertEquals(0, result.size());
    }

    @Test
    void getSubProjects_shouldFilterByNameAndStatus() {
        Project parent = new Project();
        parent.setId(1L);
        parent.setVisibility(ProjectVisibility.PUBLIC);

        Project child1 = new Project();
        child1.setName("Alpha");
        child1.setStatus(ProjectStatus.CREATED);
        child1.setVisibility(ProjectVisibility.PUBLIC);

        Project child2 = new Project();
        child2.setName("Beta");
        child2.setStatus(ProjectStatus.IN_PROGRESS);
        child2.setVisibility(ProjectVisibility.PUBLIC);

        parent.setChildren(List.of(child1, child2));

        when(projectRepository.findById(1L)).thenReturn(Optional.of(parent));
        when(subProjectMapper.toSubProjectDto(any())).thenAnswer(invocation -> {
            Project p = invocation.getArgument(0);
            return new SubProjectDto(
                    p.getId(),
                    p.getName(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    p.getStatus(),
                    p.getVisibility(),
                    null,
                    null,
                    null,
                    null
            );
        });

        List<SubProjectDto> result = subProjectService.getSubProjects(
                1L,
                "Alpha",
                ProjectStatus.CREATED
        );

        assertEquals(1, result.size());
        assertEquals("Alpha", result.get(0).name());
    }

    @Test
    void getSubProjects_shouldFilterByVisibility() {
        Project parent = new Project();
        parent.setId(1L);
        parent.setVisibility(ProjectVisibility.PRIVATE);

        Project child1 = new Project();
        child1.setVisibility(ProjectVisibility.PRIVATE);

        Project child2 = new Project();
        child2.setVisibility(ProjectVisibility.PUBLIC);

        parent.setChildren(List.of(child1, child2));

        when(projectRepository.findById(1L)).thenReturn(Optional.of(parent));
        when(subProjectMapper.toSubProjectDto(any())).thenAnswer(invocation -> {
            Project p = invocation.getArgument(0);
            return new SubProjectDto(
                    p.getId(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    p.getStatus(),
                    p.getVisibility(),
                    null,
                    null,
                    null,
                    null
            );
        });

        List<SubProjectDto> result = subProjectService.getSubProjects(1L, null, null);

        assertEquals(1, result.size());
        assertEquals(ProjectVisibility.PRIVATE, result.get(0).visibility());
    }
}