package faang.school.projectservice.service.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectForCreationDto;
import faang.school.projectservice.dto.project.ProjectForUpdateDto;
import faang.school.projectservice.dto.project.ProjectOutputDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.filter.project.TestProjectNameFilter;
import faang.school.projectservice.filter.project.TestProjectStatusFilter;
import faang.school.projectservice.filter.project.TestProjectVisibilityFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ResourceService;
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

    @Mock
    private ResourceService resourceService;

    private final ProjectFilter visibilityFilter = new TestProjectVisibilityFilter();
    private final ProjectFilter statusFilter = new TestProjectStatusFilter();
    private final ProjectFilter nameFilter = new TestProjectNameFilter();

    @Mock
    private UserContext userContext;

    @Spy
    private ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);

    @InjectMocks
    private ProjectServiceImpl service;

    @Captor
    private ArgumentCaptor<Project> projectCaptor;

    private List<ProjectFilter> filters;
    private ProjectForCreationDto creationDto;
    private ProjectForUpdateDto updateDto;
    private ProjectOutputDto outputDto;
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
                        projectRepository, resourceService, projectMapper, userContext);
        creationDto = ProjectForCreationDto.builder().build();
        updateDto = ProjectForUpdateDto.builder().build();
        outputDto = ProjectOutputDto.builder().build();
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
    public void testGetFilteredProject_AllPrivet_ByMembership_false() {
        when(userContext.getUserId()).thenReturn(user1Id);
        when(projectRepository.findAll()).thenReturn(List.of(project1, project2));
        filterDto.setVisibility(ProjectVisibility.PRIVATE);

        List<ProjectOutputDto> result1 = service.getFilteredProjects(filterDto);

        assertEquals(0, result1.size());
    }

    @Test
    public void testGetFilteredProject_AllPrivet_ByMembership_true() {
        when(userContext.getUserId()).thenReturn(user2Id);
        when(projectRepository.findAll()).thenReturn(List.of(project1, project2));
        filterDto.setVisibility(ProjectVisibility.PRIVATE);

        List<ProjectOutputDto> result2 = service.getFilteredProjects(filterDto);

        assertEquals(2, result2.size());
    }

    @Test
    public void testGetFilteredProject_ByStatusFilter_Membership_false() {
        when(userContext.getUserId()).thenReturn(user1Id);
        when(projectRepository.findAll()).thenReturn(List.of(project1, project2));
        project1.setStatus(ProjectStatus.CREATED);
        project2.setStatus(ProjectStatus.CREATED);
        filterDto.setStatus(ProjectStatus.CREATED);

        List<ProjectOutputDto> result1 = service.getFilteredProjects(filterDto);

        assertEquals(0, result1.size());
    }

    @Test
    public void testGetFilteredProject_ByStatusFilter_Membership_true() {
        when(userContext.getUserId()).thenReturn(user2Id);
        when(projectRepository.findAll()).thenReturn(List.of(project1, project2));
        project1.setStatus(ProjectStatus.CREATED);
        project2.setStatus(ProjectStatus.CREATED);
        filterDto.setStatus(ProjectStatus.CREATED);

        List<ProjectOutputDto> result2 = service.getFilteredProjects(filterDto);

        assertEquals(2, result2.size());
    }

    @Test
    public void testGetFilteredProject_ByTwoFilters_Membership_false() {
        project1.setStatus(ProjectStatus.CREATED);
        project1.setName("Bakery");
        project2.setStatus(ProjectStatus.CANCELLED);
        project2.setName("Bakery");
        project3.setStatus(ProjectStatus.CREATED);
        project3.setName("Forge");
        when(projectRepository.findAll()).thenReturn(List.of(project1, project2, project3));

        filterDto.setStatus(ProjectStatus.CREATED);
        filterDto.setName("Bakery");

        List<ProjectOutputDto> result1 = service.getFilteredProjects(filterDto);

        assertEquals(0, result1.size());
    }

    @Test
    public void testGetFilteredProject_ByTwoFilters_Membership_true() {
        project1.setStatus(ProjectStatus.CREATED);
        project1.setName("Bakery");
        project2.setStatus(ProjectStatus.CANCELLED);
        project2.setName("Bakery");
        project3.setStatus(ProjectStatus.CREATED);
        project3.setName("Forge");
        when(projectRepository.findAll()).thenReturn(List.of(project1, project2, project3));
        when(userContext.getUserId()).thenReturn(user2Id);

        filterDto.setStatus(ProjectStatus.CREATED);
        filterDto.setName("Bakery");

        List<ProjectOutputDto> result2 = service.getFilteredProjects(filterDto);

        assertEquals(1, result2.size());
    }

    @Test
    public void testCreate_OwnerAlreadyHasProject_WithSameName_Uncancelled() {
        String name = "Bakery";
        Optional<List<Project>> sameNamedProjects = Optional.of(List.of(project1));
        project1.setStatus(ProjectStatus.CREATED);
        project1.setName(name);
        when(projectRepository.findByNameAndOwnerId(name, user2Id)).thenReturn(sameNamedProjects);
        when(userContext.getUserId()).thenReturn(user2Id);
        creationDto.setName(name);
        creationDto.setOwnerId(user2Id);

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                service.create(creationDto));

        assertEquals(String.format
                        ("User with id = %d already has a project named %s", user2Id,
                                creationDto.getName()),
                exception.getMessage());
    }

    @Test
    public void testCreate_OwnerAlreadyHasProject_WithSameName_Cancelled() {
        String name = "Bakery";
        Optional<List<Project>> sameNamedProjects = Optional.of(List.of(project1));
        project1.setStatus(ProjectStatus.CANCELLED);
        project1.setName(name);
        when(projectRepository.findByNameAndOwnerId(name, user2Id)).thenReturn(sameNamedProjects);
        when(userContext.getUserId()).thenReturn(user2Id);
        creationDto.setName(name);
        creationDto.setOwnerId(user2Id);

        service.create(creationDto);

        verify(projectRepository, times(1)).save(projectCaptor.capture());
        assertEquals(project1.getName(), projectCaptor.getValue().getName());
        assertEquals(project1.getOwnerId(), projectCaptor.getValue().getOwnerId());
        assertEquals(ProjectStatus.CREATED, projectCaptor.getValue().getStatus());
    }

    @Test
    public void testCreate_OwnerHasNotProject_WithSameName() {
        when(userContext.getUserId()).thenReturn(user2Id);
        when(projectRepository.findByNameAndOwnerId("Bakery", user2Id)).thenReturn(Optional.empty());
        creationDto.setName("Bakery");
        creationDto.setOwnerId(user2Id);

        service.create(creationDto);

        verify(projectRepository, times(1)).save(projectCaptor.capture());
        assertEquals(creationDto.getName(), projectCaptor.getValue().getName());
        assertEquals(creationDto.getOwnerId(), projectCaptor.getValue().getOwnerId());
        assertEquals(ProjectStatus.CREATED, projectCaptor.getValue().getStatus());
    }

    @Test
    public void testUpdate_ProjectNotFound() {
        updateDto.setId(nonExistentProjectId);

        when(projectRepository.findById(nonExistentProjectId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> service.update(updateDto));

        assertEquals("No project with id %d has been found".formatted(nonExistentProjectId), exception.getMessage());
    }

    @Test
    public void testUpdate_ByNotAnOwner() {
        when(projectRepository.findById(project1Id)).thenReturn(Optional.of(project1));
        updateDto.setId(project1Id);
        when(userContext.getUserId()).thenReturn(user1Id);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> service.update(updateDto));

        assertEquals("Projects can be changed only be theirs owners", exception.getMessage());
    }

    @Test
    public void testUpdate_OnlyOneField() {
        updateDto.setId(project1Id);
        updateDto.setDescription("bla-bla-bla");
        project1.setDescription("bla");
        when(projectRepository.findById(project1Id)).thenReturn(Optional.of(project1));
        when(userContext.getUserId()).thenReturn(user2Id);

        service.update(updateDto);

        verify(projectRepository, times(1)).save(projectCaptor.capture());
        Project project = projectCaptor.getValue();

        assertNotNull(project.getUpdatedAt());
        assertEquals("bla-bla-bla", project.getDescription());
    }

    @Test
    public void testUpdate_ManyFields() {
        updateDto.setId(project1Id);
        updateDto.setName("Bakery");
        updateDto.setDescription("bla");
        updateDto.setStatus(ProjectStatus.ON_HOLD);
        updateDto.setVisibility(ProjectVisibility.PUBLIC);
        project1.setDescription("bla-bla-bla");
        project1.setName("Forge");
        project1.setStatus(ProjectStatus.CREATED);
        when(projectRepository.findById(project1Id)).thenReturn(Optional.of(project1));
        when(userContext.getUserId()).thenReturn(user2Id);

        ProjectOutputDto outputDto = service.update(updateDto);

        verify(projectRepository, times(1)).save(projectCaptor.capture());
        Project project = projectCaptor.getValue();
        ProjectOutputDto projectDto1 = projectMapper.toProjectDto(project);

        assertNotNull(project.getUpdatedAt());
        assertEquals(updateDto.getDescription(), project.getDescription());
        assertEquals(updateDto.getVisibility(), project.getVisibility());
        assertEquals(updateDto.getStatus(), project.getStatus());
        assertEquals(projectDto1, outputDto);
    }

    @Test
    public void testGetProjectById_ProjectNotFound(){
        when(projectRepository.findById(nonExistentProjectId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> service.getProjectById(nonExistentProjectId));

        assertEquals("No project with this id has been found", exception.getMessage());
    }

    @Test
    public void testGetProjectById_NotAnOwner(){
        when(projectRepository.findById(any())).thenReturn(Optional.of(project1));
        when(userContext.getUserId()).thenReturn(user1Id);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> service.getProjectById(project1Id));

        assertEquals("All privet projects are visible only for members", exception.getMessage());
    }

    @Test
    public void testGetProjectById_AnOwner(){
        when(projectRepository.findById(any())).thenReturn(Optional.of(project1));
        when(userContext.getUserId()).thenReturn(user2Id);

        ProjectOutputDto result = service.getProjectById(project1Id);
        ProjectOutputDto projectDto1 = projectMapper.toProjectDto(project1);

        assertEquals(projectDto1, result);
    }
}