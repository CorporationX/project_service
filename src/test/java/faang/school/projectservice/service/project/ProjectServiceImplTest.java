package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.filter.projecfilters.TestProjectNameFilter;
import faang.school.projectservice.filter.projecfilters.TestProjectStatusFilter;
import faang.school.projectservice.filter.projecfilters.TestProjectVisibilityFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    private final ProjectFilter visibilityFilter = new TestProjectVisibilityFilter();
    private final ProjectFilter statusFilter = new TestProjectStatusFilter();
    private final ProjectFilter nameFilter = new TestProjectNameFilter();

    @Spy
    private ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);

    @InjectMocks
    private ProjectServiceImpl service;

    @Captor
    private ArgumentCaptor<Project> projectCaptor;

    private List<ProjectFilter> filters;
    private ProjectDto projectDto;
    private ProjectFilterDto filterDto;
    private long user1Id = 1L;
    private long user2Id = 2L;
    private long project1Id = 111L;
    private long project2Id = 222L;
    private long project3Id = 333L;
    private long nonExistentProjectId = 999L;
    private Project project1;
    private Project project2;
    private Project project3;

    @BeforeEach
    public void setUp() {
        service = new ProjectServiceImpl
                (List.of(visibilityFilter, statusFilter, nameFilter),
                        projectRepository, projectMapper);
        projectDto = ProjectDto.builder().build();
        filterDto = ProjectFilterDto.builder().build();
        project1 = Project.builder()
                .id(project1Id)
                .visibility(ProjectVisibility.PRIVATE)
                .ownerId(user2Id).build();
        project2 = Project.builder()
                .id(project2Id)
                .visibility(ProjectVisibility.PRIVATE)
                .ownerId(user2Id)
                .build();
        project3 = Project.builder()
                .id(project3Id)
                .visibility(ProjectVisibility.PRIVATE)
                .ownerId(user2Id)
                .build();
    }

    @Test
    public void testGetFilteredProject_AllPrivet_ByMembership() {
        when(projectRepository.findAll()).thenReturn(List.of(project1, project2));
        filterDto.setVisibility(ProjectVisibility.PRIVATE);

        List<ProjectDto> result1 = service.getFilteredProjects(user1Id, filterDto);
        List<ProjectDto> result2 = service.getFilteredProjects(user2Id, filterDto);

        assertEquals(0, result1.size());
        assertEquals(2, result2.size());
    }

    @Test
    public void testGetFilteredProject_ByStatusFilter() {
        when(projectRepository.findAll()).thenReturn(List.of(project1, project2));
        project1.setStatus(ProjectStatus.CREATED);
        project2.setStatus(ProjectStatus.CREATED);
        filterDto.setStatus(ProjectStatus.CREATED);

        List<ProjectDto> result1 = service.getFilteredProjects(user1Id, filterDto);
        List<ProjectDto> result2 = service.getFilteredProjects(user2Id, filterDto);

        assertEquals(0, result1.size());
        assertEquals(2, result2.size());
    }

    @Test
    public void testGetFilteredProject_ByTwoFilters() {
        project1.setStatus(ProjectStatus.CREATED);
        project1.setName("Bakery");
        project2.setStatus(ProjectStatus.CANCELLED);
        project2.setName("Bakery");
        project3.setStatus(ProjectStatus.CREATED);
        project3.setName("Forge");
        when(projectRepository.findAll()).thenReturn(List.of(project1, project2, project3));

        filterDto.setStatus(ProjectStatus.CREATED);
        filterDto.setName("Bakery");

        List<ProjectDto> result1 = service.getFilteredProjects(user1Id, filterDto);
        List<ProjectDto> result2 = service.getFilteredProjects(user2Id, filterDto);

        assertEquals(0, result1.size());
        assertEquals(1, result2.size());
    }

    @Test
    public void testCreate_OwnerAlreadyHasProject_WithSameName_Uncancelled() {
        project1.setStatus(ProjectStatus.CREATED);
        project1.setName("Bakery");
        when(projectRepository.findAll()).thenReturn(List.of(project1));
        projectDto.setName("Bakery");
        projectDto.setOwnerId(user2Id);

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                service.create(projectDto));

        assertEquals(String.format
                        ("User with id = %d already has a project named %s", user2Id,
                                projectDto.getName()),
                exception.getMessage());
    }

    @Test
    public void testCreate_OwnerAlreadyHasProject_WithSameName_Cancelled() {
        project1.setStatus(ProjectStatus.CANCELLED);
        project1.setName("Bakery");
        when(projectRepository.findAll()).thenReturn(List.of(project1));
        projectDto.setName("Bakery");
        projectDto.setOwnerId(user2Id);

        service.create(projectDto);

        verify(projectRepository, times(1)).save(projectCaptor.capture());
        assertEquals(project1.getName(), projectCaptor.getValue().getName());
        assertEquals(project1.getOwnerId(), projectCaptor.getValue().getOwnerId());
        assertEquals(ProjectStatus.CREATED, projectCaptor.getValue().getStatus());
    }

    @Test
    public void testCreate_OwnerHasNotProject_WithSameName() {
        when(projectRepository.findAll()).thenReturn(List.of());
        projectDto.setName("Bakery");
        projectDto.setOwnerId(user2Id);

        service.create(projectDto);

        verify(projectRepository, times(1)).save(projectCaptor.capture());
        assertEquals(projectDto.getName(), projectCaptor.getValue().getName());
        assertEquals(projectDto.getOwnerId(), projectCaptor.getValue().getOwnerId());
        assertEquals(ProjectStatus.CREATED, projectCaptor.getValue().getStatus());
    }

    @Test
    public void testUpdate_ProjectNotFound() {
        projectDto.setId(nonExistentProjectId);

        when(projectRepository.findById(nonExistentProjectId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> service.update(projectDto));

        assertEquals("No project with this id has been found", exception.getMessage());
    }

    @Test
    public void testUpdate_ByNotAnOwner() {
        when(projectRepository.findById(project1Id)).thenReturn(Optional.of(project1));
        projectDto.setId(project1Id);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> service.update(projectDto));

        assertEquals("Projects can be changed only be theirs owners", exception.getMessage());
    }

    @Test
    public void testUpdate_OnlyOneField() {
        projectDto.setId(project1Id);
        projectDto.setOwnerId(user2Id);
        projectDto.setDescription("bla-bla-bla");
        project1.setDescription("bla");
        when(projectRepository.findById(project1Id)).thenReturn(Optional.of(project1));

        service.update(projectDto);

        verify(projectRepository, times(1)).save(projectCaptor.capture());
        Project project = projectCaptor.getValue();

        assertNotNull(project.getUpdatedAt());
        assertEquals("bla-bla-bla", project.getDescription());
    }

    @Test
    public void testUpdate_ManyFields() {
        projectDto.setId(project1Id);
        projectDto.setOwnerId(user2Id);
        projectDto.setName("Bakery");
        projectDto.setDescription("bla");
        projectDto.setStatus(ProjectStatus.ON_HOLD);
        projectDto.setVisibility(ProjectVisibility.PUBLIC);

        project1.setDescription("bla-bla-bla");
        project1.setName("Forge");
        project1.setStatus(ProjectStatus.CREATED);
        when(projectRepository.findById(project1Id)).thenReturn(Optional.of(project1));

        ProjectDto updated = service.update(projectDto);

        verify(projectRepository, times(1)).save(projectCaptor.capture());
        Project project = projectCaptor.getValue();
        ProjectDto projectDto1 = projectMapper.toProjectDto(project);

        assertNotNull(project.getUpdatedAt());
        assertEquals(projectDto.getDescription(), project.getDescription());
        assertEquals(projectDto.getVisibility(), project.getVisibility());
        assertEquals(projectDto.getStatus(), project.getStatus());

        assertEquals(projectDto1, updated);
    }

    @Test
    public void testGetProjectById_ProjectNotFound(){
        when(projectRepository.findById(nonExistentProjectId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> service.getProjectById(user1Id, nonExistentProjectId));

        assertEquals("No project with this id has been found", exception.getMessage());
    }

    @Test
    public void testGetProjectById_NotAnOwner(){
        when(projectRepository.findById(any())).thenReturn(Optional.of(project1));

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> service.getProjectById(user1Id, project1Id));

        assertEquals("All privet projects are visible only for members", exception.getMessage());
    }

    @Test
    public void testGetProjectById_AnOwner(){
        when(projectRepository.findById(any())).thenReturn(Optional.of(project1));

        ProjectDto result = service.getProjectById(user2Id, project1Id);
        ProjectDto projectDto1 = projectMapper.toProjectDto(project1);

        assertEquals(projectDto1, result);
    }
}