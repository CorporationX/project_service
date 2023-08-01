package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.dto.SubProjectFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.MomentService;
import faang.school.projectservice.service.project.filter.ProjectNameFilter;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import faang.school.projectservice.model.ProjectStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private MomentService momentService;
    @Spy
    private ProjectMapper projectMapper = new ProjectMapperImpl();
    @InjectMocks
    private ProjectService projectService;

    private ProjectDto projectDto;
    private final long userId = 1;
    private final long projectId = 1;

    @BeforeEach
    public void initProject() {
        projectDto = ProjectDto.builder()
                .id(projectId)
                .name("Project")
                .description("Cool")
                .ownerId(userId)
                .status(ProjectStatus.CREATED)
                .build();
    }

    @Test
    public void shouldReturnProjectsList() {
        List<Project> desiredProjects = List.of(new Project());
        when(projectRepository.findAll()).thenReturn(desiredProjects.stream());

        List<ProjectDto> receivedProject = projectService.getAllProjects();
        Assertions.assertEquals(projectMapper.toDtoList(desiredProjects), receivedProject);
    }

    @Test
    public void shouldReturnProjectByProjectId() {
        Project desiredProject = new Project();

        when(projectRepository.existsById(projectId))
                .thenReturn(true);
        when(projectRepository.getProjectById(projectId))
                .thenReturn(desiredProject);

        ProjectDto receivedProject = projectService.getProject(projectId);

        Assertions.assertEquals(projectMapper.toDto(desiredProject), receivedProject);
        Mockito.verify(projectRepository).getProjectById(projectId);
    }

    @Test
    public void shouldThrowExceptionWhenProjectNotExists() {
        when(projectRepository.existsById(projectId))
                .thenReturn(false);

        Assertions.assertThrows(DataValidationException.class, () -> projectService.getProject(projectId));
        Mockito.verify(projectRepository, Mockito.times(0)).getProjectById(projectId);
    }

    @Test
    public void shouldCreatedAndReturnNewProject() {
        when(projectRepository.existsByOwnerUserIdAndName(userId, "Project"))
                .thenReturn(false);

        Project desiredProject = projectMapper.toEntity(projectDto);

        Mockito.lenient().when(projectRepository.save(desiredProject))
                .thenReturn(desiredProject);

        ProjectDto receivedProject = projectService.createProject(projectDto);

        Assertions.assertEquals(projectMapper.toDto(desiredProject), receivedProject);
        Mockito.verify(projectRepository).save(projectMapper.toEntity(projectDto));
    }

    @Test
    public void shouldThrowExceptionWhenProjectExists() {
        ProjectDto testProjectDto = projectDto;

        when(projectRepository.existsByOwnerUserIdAndName(testProjectDto.getId(), testProjectDto.getName()))
                .thenReturn(true);

        Assertions.assertThrows(DataValidationException.class, () -> projectService.createProject(testProjectDto));
        Mockito.verify(projectRepository, Mockito.times(0))
                .save(projectMapper.toEntity(projectDto));
    }

    @Test
    public void shouldReturnAndUpdateProject() {
        Project newProject = projectMapper.toEntity(projectDto);
        newProject.setName("Mega project");

        Project updatedProject = projectMapper.toEntity(projectDto);
        projectMapper.updateFromDto(projectMapper.toDto(newProject), updatedProject);

        Optional<Project> returnedOptional = Optional.of(projectMapper.toEntity(projectDto));

        Mockito.when(projectRepository.findById(projectId))
                .thenReturn(returnedOptional);
        Mockito.when(projectRepository.save(updatedProject))
                .thenReturn(updatedProject);

        ProjectDto receivedProject = projectService.updateProject(projectMapper.toDto(newProject), projectId);

        Assertions.assertEquals(projectMapper.toDto(returnedOptional.get()), receivedProject);
    }

    @Test
    public void shouldThrowExceptionWhenOptionalIsEmpty() {
        Mockito.when(projectRepository.findById(projectId))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> projectService.updateProject(projectDto, projectId));
    }

    @Test
    void testCrateSubProject() {
        ProjectDto subProjectDto = ProjectDto.builder()
                .name("SubProject")
                .visibility(ProjectVisibility.PRIVATE)
                .build();
        Project subProject = projectMapper.toEntity(subProjectDto);
        Project parentProject = Project.builder()
                .visibility(ProjectVisibility.PRIVATE)
                .children(new ArrayList<>())
                .build();
        subProject.setParentProject(parentProject);
        subProject.setStatus(ProjectStatus.CREATED);
        ProjectDto subProjectDtoExpected = projectMapper.toDto(subProject);


        when(projectRepository.existsById(subProjectDto.getParentId())).thenReturn(true);
        when(projectRepository.getProjectById(subProjectDto.getParentId())).thenReturn(parentProject);
        when(projectRepository.save(subProject)).thenReturn(subProject);

        ProjectDto projectDtoActual = projectService.createSubProject(subProjectDto);

        assertNotNull(projectDtoActual);
        assertEquals(subProjectDtoExpected, projectDtoActual);
        assertEquals("SubProject", projectDtoActual.getName());
        assertEquals(ProjectVisibility.PRIVATE, projectDtoActual.getVisibility());
    }

    @Test
    void testValidateParentProjectExists() {
        ProjectDto subProjectDto = new ProjectDto();

        when(projectRepository.existsById(subProjectDto.getParentId())).thenReturn(false);

        assertThrows(DataValidationException.class,
                () -> projectService.createSubProject(subProjectDto), "No such parent project");
    }

    @Test
    void testValidateVisibilityConsistency() {
        ProjectDto subProjectDto = new ProjectDto();
        subProjectDto.setVisibility(ProjectVisibility.PUBLIC);

        Project parentProject = new Project();
        parentProject.setVisibility(ProjectVisibility.PRIVATE);

        when(projectRepository.existsById(subProjectDto.getParentId())).thenReturn(true);
        when(projectRepository.getProjectById(subProjectDto.getParentId())).thenReturn(parentProject);

        DataValidationException validationException = assertThrows(DataValidationException.class,
                () -> projectService.createSubProject(subProjectDto));
        assertEquals("The visibility of the subproject must be - " +
                parentProject.getVisibility() + " like the parent project", validationException.getMessage());
    }

    @Test
    void testValidateSubProjectUnique() {
        ProjectDto subProjectDto = new ProjectDto();
        subProjectDto.setVisibility(ProjectVisibility.PRIVATE);
        subProjectDto.setName("SubProject");

        Project childProject = new Project();
        childProject.setName("SubProject");

        Project parentProject = new Project();
        parentProject.setVisibility(ProjectVisibility.PRIVATE);
        parentProject.setChildren(List.of(childProject));

        when(projectRepository.existsById(subProjectDto.getParentId())).thenReturn(true);
        when(projectRepository.getProjectById(subProjectDto.getParentId())).thenReturn(parentProject);

        DataValidationException validationException = assertThrows(DataValidationException.class,
                () -> projectService.createSubProject(subProjectDto));
        assertEquals("Subproject with name " + subProjectDto.getName() + " already exists",
                validationException.getMessage());
    }

    @Test
    public void shouldReturnProjectsListForFilters() {
        ProjectFilterDto filters = new ProjectFilterDto();
        filters.setNamePattern("Uni");

        List<Project> projectsFromDB = List.of(
                Project.builder()
                        .name("Unicorn project")
                        .build(),
                Project.builder()
                        .name("Kraken project")
                        .build()
        );
        Stream<Project> projectFromBDStream = projectsFromDB.stream();
        Stream<Project> desiredProjects = getFilteredProjects(projectsFromDB);

        ProjectNameFilter nameFilter = Mockito.mock(ProjectNameFilter.class);
        projectService = new ProjectService(projectRepository, projectMapper, momentService, List.of(nameFilter), List.of());

        Mockito.when(projectRepository.findAll()).thenReturn(projectFromBDStream);

        Mockito.when(nameFilter.isApplicable(filters)).thenReturn(true);
        Mockito.when(nameFilter.apply(projectFromBDStream, filters))
                .thenReturn(desiredProjects);

        List<ProjectDto> receivedProjects = projectService.getProjects(filters);

        desiredProjects = getFilteredProjects(projectsFromDB);
        Assertions.assertEquals(projectMapper.toDtoList(desiredProjects.toList()), receivedProjects);
    }

    private Stream<Project> getFilteredProjects(List<Project> projects) {
        return projects.stream()
                .filter(project -> project.getName().contains("Uni"));
    }

    @Test
    void testUpdateSubProjectNotCompletedStatus() {
        ProjectDto subProjectDto = ProjectDto.builder()
                .id(1L)
                .status(ProjectStatus.CREATED)
                .parentId(2L)
                .build();
        Project subProject = projectMapper.toEntity(subProjectDto);
        Project parentProject = Project.builder()
                .id(2L)
                .build();
        subProject.setParentProject(parentProject);
        ProjectDto subProjectDtoExpected = projectMapper.toDto(subProject);

        when(projectRepository.existsById(subProjectDto.getId())).thenReturn(true);
        when(projectRepository.existsById(parentProject.getId())).thenReturn(true);
        when(projectRepository.getProjectById(subProjectDto.getId())).thenReturn(subProject);
        when(projectRepository.save(subProject)).thenReturn(subProject);

        ProjectDto projectDtoActual = projectService.updateSubProject(subProjectDto);

        verify(projectMapper, times(1))
                .updateFromDtoWithoutStatus(subProjectDto, subProject);
        assertNotNull(projectDtoActual);
        assertEquals(subProjectDtoExpected, projectDtoActual);
        assertEquals(ProjectStatus.CREATED, projectDtoActual.getStatus());
    }

    @Test
    void testUpdateSubProjectCompletedStatus() {
        ProjectDto subProjectDto = ProjectDto.builder()
                .id(1L)
                .status(ProjectStatus.COMPLETED)
                .parentId(2L)
                .childrenId(List.of(1L))
                .build();
        Project subProject = projectMapper.toEntity(subProjectDto);
        Project parentProject = Project.builder()
                .id(2L)
                .build();

        when(projectRepository.existsById(subProjectDto.getId())).thenReturn(true);
        when(projectRepository.existsById(parentProject.getId())).thenReturn(true);
        when(projectRepository.getProjectById(subProjectDto.getId())).thenReturn(subProject);

        projectService.updateSubProject(subProjectDto);

        verify(momentService, times(1)).createMomentCompletedForSubProject(subProject);
    }

    @Test
    void testGetFilteredSubProjects() {
        Long projectId = 1L;
        List<Project> subProjects = List.of(
                Project.builder()
                        .name("SubProject1")
                        .status(ProjectStatus.CREATED)
                        .build(),
                Project.builder()
                        .name("SubProject2")
                        .status(ProjectStatus.IN_PROGRESS)
                        .build(),
                Project.builder()
                        .name("SubProject3")
                        .status(ProjectStatus.COMPLETED)
                        .build(),
                Project.builder()
                        .name("subProject4")
                        .status(ProjectStatus.CANCELLED)
                        .build()
        );
        Project parentProject = Project.builder()
                .id(projectId)
                .children(subProjects)
                .build();
        SubProjectFilterDto filtersDto = SubProjectFilterDto.builder()
                .projectId(projectId)
                .nameFilter("Sub")
                .statusFilter(List.of(ProjectStatus.IN_PROGRESS, ProjectStatus.COMPLETED))
                .build();
        SubProjectFilter nameFilter = new SubProjectNameFilter();
        SubProjectFilter statusFilter = new SubProjectStatusFilter();
        projectService = new ProjectService(projectRepository, projectMapper, momentService,
                List.of(), List.of(nameFilter, statusFilter));

        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(projectRepository.getProjectById(projectId)).thenReturn(parentProject);

        List<ProjectDto> actualProjects = projectService.getFilteredSubProjects(filtersDto);

        assertEquals(2, actualProjects.size());
        assertEquals("SubProject2", actualProjects.get(0).getName());
        assertEquals(ProjectStatus.COMPLETED, actualProjects.get(1).getStatus());
    }
}
