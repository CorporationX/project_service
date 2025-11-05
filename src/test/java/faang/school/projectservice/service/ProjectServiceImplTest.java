package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.CreateProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.UpdateProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.ProjectServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectMapper mapper;
    @Mock
    private UserContext userContext;

    @InjectMocks
    private ProjectServiceImpl service;

    @Test
    void create_success_setsStatusCreated_andMaps() {
        long requesterId = 10L;
        when(userContext.getUserId()).thenReturn(10L);

        CreateProjectDto dto = new CreateProjectDto(
                " New Project ",
                "some description",
                ProjectVisibility.PUBLIC
        );

        when(projectRepository.existsByOwnerIdAndName(requesterId, "New Project")).thenReturn(false);

        Project mapped = new Project();
        when(mapper.toProject(eq(dto))).thenReturn(mapped);

        ArgumentCaptor<Project> saveCaptor = ArgumentCaptor.forClass(Project.class);
        when(projectRepository.save(saveCaptor.capture())).thenAnswer(inv -> {
            Project p = saveCaptor.getValue();
            p.setId(123L);
            return p;
        });

        ProjectDto outDto = new ProjectDto(
                123L,
                "New Project",
                "some description",
                ProjectStatus.CREATED,
                ProjectVisibility.PUBLIC
        );
        when(mapper.toProjectDto(any(Project.class))).thenReturn(outDto);

        ProjectDto result = service.create(requesterId, dto);

        assertSame(outDto, result);
        assertEquals(ProjectStatus.CREATED, saveCaptor.getValue().getStatus());
        assertEquals(requesterId, saveCaptor.getValue().getOwnerId());
        assertEquals("New Project", saveCaptor.getValue().getName());
        assertEquals("some description", saveCaptor.getValue().getDescription());

        verify(userContext).getUserId();
        verify(mapper).toProject(dto);
        verify(projectRepository).existsByOwnerIdAndName(requesterId, "New Project");
        verify(projectRepository).save(any(Project.class));
        verify(mapper).toProjectDto(any(Project.class));
        verifyNoMoreInteractions(projectRepository, mapper, userContext);
    }

    @Test
    void create_fails_whenBlankName() {
        when(userContext.getUserId()).thenReturn(7L);

        CreateProjectDto dto = new CreateProjectDto(
                "   ",
                "desc",
                ProjectVisibility.PUBLIC
        );

        DataValidationException ex = assertThrows(DataValidationException.class, () -> service.create(7L, dto));
        assertEquals("name should be present", ex.getMessage());

        verify(userContext).getUserId();
        verifyNoInteractions(projectRepository, mapper);
    }

    @Test
    void create_fails_whenUserMismatch() {
        when(userContext.getUserId()).thenReturn(1L);

        CreateProjectDto dto = new CreateProjectDto(
                "Project",
                "desc",
                ProjectVisibility.PUBLIC
        );

        ForbiddenException ex = assertThrows(ForbiddenException.class, () -> service.create(2L, dto));
        assertEquals("User 2 doesn't match profile owner", ex.getMessage());

        verify(userContext).getUserId();
        verifyNoInteractions(projectRepository, mapper);
    }

    @Test
    void create_fails_whenDuplicateName_forRequesterId() {
        long requesterId = 5L;
        when(userContext.getUserId()).thenReturn(5L);

        CreateProjectDto dto = new CreateProjectDto(
                "Project",
                "desc",
                ProjectVisibility.PRIVATE
        );

        when(projectRepository.existsByOwnerIdAndName(5L, "Project")).thenReturn(true);

        DataValidationException ex = assertThrows(DataValidationException.class, () -> service.create(requesterId, dto));
        assertEquals("You already have a project with this name", ex.getMessage());

        verify(userContext).getUserId();
        verify(projectRepository).existsByOwnerIdAndName(5L, "Project");
        verifyNoMoreInteractions(projectRepository);
        verifyNoInteractions(mapper);
    }

    @Test
    void update_allFieldsProvided_updatesAll() {
        long requesterId = 9L;
        long projectId = 77L;
        when(userContext.getUserId()).thenReturn(9L);

        UpdateProjectDto dto = new UpdateProjectDto(
                " New Project ",
                "updated description",
                ProjectStatus.IN_PROGRESS,
                ProjectVisibility.PRIVATE
        );

        Project existing = new Project();
        existing.setId(projectId);
        existing.setName("Old Project");
        existing.setDescription("Old description");
        existing.setStatus(ProjectStatus.CREATED);
        existing.setVisibility(ProjectVisibility.PUBLIC);
        existing.setOwnerId(9L);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existing));
        when(projectRepository.existsByOwnerIdAndName(9L, "New Project")).thenReturn(false);

        ArgumentCaptor<Project> saveCaptor = ArgumentCaptor.forClass(Project.class);
        when(projectRepository.save(saveCaptor.capture())).thenAnswer(inv -> saveCaptor.getValue());

        ProjectDto out = new ProjectDto(
                projectId,
                "New Project",
                "updated description",
                ProjectStatus.IN_PROGRESS,
                ProjectVisibility.PRIVATE
        );
        when(mapper.toProjectDto(any(Project.class))).thenReturn(out);

        ProjectDto result = service.update(requesterId, projectId, dto);

        assertSame(out, result);
        Project saved = saveCaptor.getValue();
        assertEquals("New Project", saved.getName());
        assertEquals("updated description", saved.getDescription());
        assertEquals(ProjectStatus.IN_PROGRESS, saved.getStatus());
        assertEquals(ProjectVisibility.PRIVATE, saved.getVisibility());

        verify(userContext).getUserId();
        verify(projectRepository).findById(projectId);
        verify(projectRepository).existsByOwnerIdAndName(9L, "New Project");
        verify(projectRepository).save(any(Project.class));
        verify(mapper).toProjectDto(any(Project.class));
        verifyNoMoreInteractions(projectRepository, mapper, userContext);
    }

    @Test
    void update_noFieldsProvided_isAllowed_andSaves() {
        long requesterId = 3L;
        long projectId = 12L;
        when(userContext.getUserId()).thenReturn(3L);

        UpdateProjectDto dto = new UpdateProjectDto(null, null, null, null);

        Project existing = new Project();
        existing.setId(projectId);
        existing.setName("Keep");
        existing.setStatus(ProjectStatus.CREATED);
        existing.setVisibility(ProjectVisibility.PUBLIC);
        existing.setOwnerId(3L);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existing));
        when(projectRepository.save(existing)).thenReturn(existing);

        ProjectDto out = new ProjectDto(
                projectId,
                "Keep",
                null,
                ProjectStatus.CREATED,
                ProjectVisibility.PUBLIC
        );
        when(mapper.toProjectDto(existing)).thenReturn(out);

        assertDoesNotThrow(() -> service.update(requesterId, projectId, dto));

        verify(userContext).getUserId();
        verify(projectRepository).findById(projectId);
        verify(projectRepository).save(existing);
        verify(mapper).toProjectDto(existing);
        verifyNoMoreInteractions(projectRepository, mapper, userContext);
    }

    @Test
    void update_nameBlank_throws() {
        when(userContext.getUserId()).thenReturn(1L);

        UpdateProjectDto dto = new UpdateProjectDto("   ", null, null, null);

        Project existing = new Project();
        existing.setId(1L);
        existing.setName("Old");
        existing.setOwnerId(1L);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(existing));

        DataValidationException ex = assertThrows(DataValidationException.class, () -> service.update(1L, 1L, dto));
        assertEquals("name should be present", ex.getMessage());

        verify(userContext).getUserId();
        verify(projectRepository).findById(1L);
        verifyNoMoreInteractions(projectRepository);
        verifyNoInteractions(mapper);
    }

    @Test
    void update_nameSameAsExisting_doesNotCheckDuplicates() {
        long requesterId = 6L;
        long projectId = 2L;
        when(userContext.getUserId()).thenReturn(6L);

        UpdateProjectDto dto = new UpdateProjectDto(" Same ", null, null, null);

        Project existing = new Project();
        existing.setId(projectId);
        existing.setName("Same");
        existing.setOwnerId(6L);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existing));
        when(projectRepository.save(any(Project.class))).thenAnswer(inv -> inv.getArgument(0));

        ProjectDto out = new ProjectDto(
                projectId,
                "Same",
                null,
                existing.getStatus(),
                existing.getVisibility()
        );
        when(mapper.toProjectDto(any(Project.class))).thenReturn(out);

        service.update(requesterId, projectId, dto);

        verify(userContext).getUserId();
        verify(projectRepository).findById(projectId);
        verify(projectRepository, never()).existsByOwnerIdAndName(anyLong(), anyString());
        verify(projectRepository).save(any(Project.class));
        verify(mapper).toProjectDto(any(Project.class));
        verifyNoMoreInteractions(projectRepository, mapper, userContext);
    }

    @Test
    void update_nameChanged_duplicate_throws() {
        long requesterId = 4L;
        when(userContext.getUserId()).thenReturn(4L);

        UpdateProjectDto dto = new UpdateProjectDto(" New ", null, null, null);

        Project existing = new Project();
        existing.setId(9L);
        existing.setName("Old");
        existing.setOwnerId(4L);

        when(projectRepository.findById(9L)).thenReturn(Optional.of(existing));
        when(projectRepository.existsByOwnerIdAndName(4L, "New")).thenReturn(true);

        DataValidationException ex = assertThrows(DataValidationException.class, () -> service.update(requesterId, 9L, dto));
        assertEquals("You already have a project with this name", ex.getMessage());

        verify(userContext).getUserId();
        verify(projectRepository).findById(9L);
        verify(projectRepository).existsByOwnerIdAndName(4L, "New");
        verifyNoMoreInteractions(projectRepository);
        verifyNoInteractions(mapper);
    }

    @Test
    void update_notFound_throws() {
        when(userContext.getUserId()).thenReturn(1L);
        UpdateProjectDto dto = new UpdateProjectDto(null, null, null, null);
        when(projectRepository.findById(999L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.update(1L, 999L, dto));
        assertEquals("Project not found: id=999", ex.getMessage());

        verify(userContext).getUserId();
        verify(projectRepository).findById(999L);
        verifyNoMoreInteractions(projectRepository);
        verifyNoInteractions(mapper);
    }

    @Test
    void update_userMismatch_throws() {
        when(userContext.getUserId()).thenReturn(1L);
        UpdateProjectDto dto = new UpdateProjectDto(null, null, null, null);

        ForbiddenException ex = assertThrows(ForbiddenException.class, () -> service.update(2L, 10L, dto));
        assertEquals("User 2 doesn't match profile owner", ex.getMessage());

        verify(userContext).getUserId();
        verifyNoInteractions(projectRepository, mapper);
    }

    @Test
    void getById_public_ok() {
        long requesterId = 100L;
        long projectId = 7L;

        Project p = new Project();
        p.setId(projectId);
        p.setOwnerId(1L);
        p.setVisibility(ProjectVisibility.PUBLIC);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(p));

        ProjectDto dto = new ProjectDto(
                projectId,
                "Any",
                "Any",
                ProjectStatus.CREATED,
                ProjectVisibility.PUBLIC
        );
        when(mapper.toProjectDto(p)).thenReturn(dto);

        ProjectDto result = service.getById(requesterId, projectId);
        assertSame(dto, result);

        verify(projectRepository).findById(projectId);
        verify(mapper).toProjectDto(p);
        verifyNoMoreInteractions(projectRepository, mapper);
    }

    @Test
    void getById_private_notOwner_throws() {
        Project p = new Project();
        p.setId(5L);
        p.setOwnerId(2L);
        p.setVisibility(ProjectVisibility.PRIVATE);

        when(projectRepository.findById(5L)).thenReturn(Optional.of(p));

        ForbiddenException ex = assertThrows(ForbiddenException.class, () -> service.getById(3L, 5L));
        assertEquals("You don't have access to this private project", ex.getMessage());

        verify(projectRepository).findById(5L);
        verifyNoMoreInteractions(projectRepository);
        verifyNoInteractions(mapper);
    }

    @Test
    void getById_notFound_throws() {
        when(projectRepository.findById(42L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.getById(1L, 42L));
        assertEquals("Project not found: id=42", ex.getMessage());

        verify(projectRepository).findById(42L);
        verifyNoMoreInteractions(projectRepository);
        verifyNoInteractions(mapper);
    }

    @Test
    void getAll_filters_andMaps() {
        long requesterId = 11L;

        Project publicOther = new Project();
        publicOther.setId(1L);
        publicOther.setOwnerId(2L);
        publicOther.setVisibility(ProjectVisibility.PUBLIC);

        Project privateOther = new Project();
        privateOther.setId(2L);
        privateOther.setOwnerId(2L);
        privateOther.setVisibility(ProjectVisibility.PRIVATE);

        Project privateMine = new Project();
        privateMine.setId(3L);
        privateMine.setOwnerId(11L);
        privateMine.setVisibility(ProjectVisibility.PRIVATE);

        when(projectRepository.findAll()).thenReturn(Arrays.asList(publicOther, privateOther, privateMine));

        ProjectDto d1 = new ProjectDto(1L, "publicOther", null, null, ProjectVisibility.PUBLIC);
        ProjectDto d3 = new ProjectDto(3L, "privateMine", null, null, ProjectVisibility.PRIVATE);

        when(mapper.toProjectDto(publicOther)).thenReturn(d1);
        when(mapper.toProjectDto(privateMine)).thenReturn(d3);

        List<ProjectDto> result = service.getAll(requesterId);

        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of(d1, d3)));

        verify(projectRepository).findAll();
        verify(mapper).toProjectDto(publicOther);
        verify(mapper).toProjectDto(privateMine);
        verifyNoMoreInteractions(projectRepository, mapper);
    }
}
