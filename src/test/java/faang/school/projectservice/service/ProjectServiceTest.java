package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.SubProjectDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.exception.DataAlreadyExistingException;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.mapper.SubProjectMapper;
import faang.school.projectservice.mapper.SubProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.filters.ProjectFilter;
import faang.school.projectservice.service.filters.ProjectFilterByName;
import faang.school.projectservice.service.filters.ProjectFilterByStatus;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    private ProjectJpaRepository projectJpaRepository;
    private ProjectRepository projectRepository;
    private SubProjectMapper subProjectMapper;
    private ProjectService projectService;
    private SubProjectDto subProjectDto;
    private UpdateSubProjectDto updateSubProjectDto;
    private Project project;
    private Project onlyWithIdProject;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        projectJpaRepository = Mockito.mock(ProjectJpaRepository.class);
        projectRepository = new ProjectRepository(projectJpaRepository);
        subProjectMapper = new SubProjectMapperImpl();
        List<ProjectFilter> projectFilters = new ArrayList<>(List.of(new ProjectFilterByName(), new ProjectFilterByStatus()));
        projectService = new ProjectService(projectRepository, subProjectMapper, projectFilters);
        this.subProjectDto = SubProjectDto.builder()
                .name("Faang")
                .description("This is Faang")
                .ownerId(1)
                .parentProjectId(2L)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        updateSubProjectDto = UpdateSubProjectDto.builder()
                .id(3L)
                .name("Faang")
                .description("This is Faang")
                .ownerId(1)
                .parentProjectId(2L)
                .visibility(ProjectVisibility.PUBLIC)
                .status(ProjectStatus.CREATED)
                .build();
        this.project = Project.builder()
                .id(2L)
                .visibility(ProjectVisibility.PUBLIC)
                .children(new ArrayList<>())
                .build();
        this.onlyWithIdProject = Project.builder()
                .id(1L)
                .build();
    }

    @Test
    void createSubProjectThrowExceptionWhenProjectIsAlreadyExist() {
        when(projectRepository.existsByOwnerUserIdAndName(subProjectDto.getOwnerId(), subProjectDto.getName()))
                .thenReturn(true);

        DataAlreadyExistingException exception = assertThrows(DataAlreadyExistingException.class, () -> projectService.createSubProject(subProjectDto));

        assertEquals("Project Faang is already exist", exception.getMessage());
    }

    @Test
    void createSubProjectThrowEntityNotFoundException() {
        when(projectRepository.existsByOwnerUserIdAndName(1L, "Faang"))
                .thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> projectService.createSubProject(subProjectDto));

        assertEquals("Project not found by id: 2", exception.getMessage());
    }

    @Test
    void createSubProjectThrowExceptionWhenTryingToCreatePublicSubProjectOnAPrivateProject() {
        subProjectDto.setVisibility(ProjectVisibility.PUBLIC);

        when(projectRepository.existsByOwnerUserIdAndName(1L, "Faang"))
                .thenReturn(false);
        when(projectJpaRepository.findById(2L))
                .thenReturn(Optional.of(Project.builder()
                        .id(10L)
                        .visibility(ProjectVisibility.PRIVATE)
                        .build()));

        DataValidationException exception = assertThrows(DataValidationException.class, () -> projectService.createSubProject(subProjectDto));

        assertEquals("Public SubProject: Faang, cant be with a private parent Project with id: 10", exception.getMessage());
    }

    @Test
    void createSubProjectTest() {
        when(projectRepository.existsByOwnerUserIdAndName(subProjectDto.getOwnerId(), subProjectDto.getName()))
                .thenReturn(false);
        when(projectJpaRepository.findById(subProjectDto.getParentProjectId()))
                .thenReturn(Optional.of(project));

        SubProjectDto result = projectService.createSubProject(subProjectDto);

        assertEquals(subProjectDto, result);
    }

    @Test
    void createSubProjectInvokesGetProjectById() {
        when(projectRepository.existsByOwnerUserIdAndName(subProjectDto.getOwnerId(), subProjectDto.getName()))
                .thenReturn(false);
        when(projectJpaRepository.findById(subProjectDto.getParentProjectId()))
                .thenReturn(Optional.of(project));

        projectService.createSubProject(subProjectDto);

        verify(projectJpaRepository, times(2)).findById(subProjectDto.getParentProjectId());
    }

    @Test
    void updateSubProjectThrowEntityNotFoundException() {
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> projectService.updateSubProject(updateSubProjectDto));

        assertEquals("Project not found by id: 3", exception.getMessage());
    }

    @Test
    void updateSubProjectThrowExceptionWhenChildrenStatusesAreNotComplete() {
        updateSubProjectDto.setStatus(ProjectStatus.COMPLETED);
        Project first = Project.builder()
                .status(ProjectStatus.COMPLETED)
                .build();
        Project second = Project.builder()
                .status(ProjectStatus.IN_PROGRESS)
                .build();
        Project returnProject = Project.builder()
                .children(List.of(first, second))
                .build();

        when(projectJpaRepository.findById(updateSubProjectDto.getId()))
                .thenReturn(Optional.of(returnProject));

        DataValidationException exception = assertThrows(DataValidationException.class, () -> projectService.updateSubProject(updateSubProjectDto));

        assertEquals("Can't close project if subProject status are not complete or cancelled", exception.getMessage());
    }

    @Test
    void updateSubProjectClosingProjectScenarioTest() {
        updateSubProjectDto.setStatus(ProjectStatus.COMPLETED);
        Project first = Project.builder()
                .id(5L)
                .status(ProjectStatus.COMPLETED)
                .build();
        Project second = Project.builder()
                .id(10L)
                .status(ProjectStatus.CANCELLED)
                .build();

        Project returnProject = Project.builder()
                .children(List.of(first, second))
                .updatedAt(LocalDateTime.now())
                .build();

        when(projectJpaRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(returnProject));

        LocalDateTime expected = returnProject.getUpdatedAt();

        LocalDateTime result = projectService.updateSubProject(updateSubProjectDto);

        assertTrue(expected.isEqual(result));

    }

    @Test
    void updateSubProjectWithPublicVisibilityScenarioTest() {
        Project returnProject = Project.builder()
                .id(5L)
                .name("Faang")
                .visibility(ProjectVisibility.PUBLIC)
                .updatedAt(LocalDateTime.now())
                .build();

        when(projectJpaRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(returnProject));

        LocalDateTime expected = returnProject.getUpdatedAt();

        LocalDateTime result = projectService.updateSubProject(updateSubProjectDto);

        assertTrue(expected.isEqual(result));
    }

    @Test
    void updateSubProjectWithPublicVisibilityAndNullParentProjectIdScenarioTest() {
        updateSubProjectDto.setParentProjectId(null);
        Project returnProject = Project.builder()
                .id(5L)
                .name("Faang")
                .parentProject(Project.builder()
                        .id(15L)
                        .build())
                .visibility(ProjectVisibility.PUBLIC)
                .updatedAt(LocalDateTime.now())
                .build();

        when(projectJpaRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(returnProject));

        LocalDateTime expected = returnProject.getUpdatedAt();

        LocalDateTime result = projectService.updateSubProject(updateSubProjectDto);

        assertTrue(expected.isEqual(result));
    }

    @Test
    void updateSubProjectWithPrivateVisibilityScenarioTest() {
        updateSubProjectDto.setVisibility(ProjectVisibility.PRIVATE);

        Project firstProjectChildren = Project.builder()
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        Project firstChildren = Project.builder()
                .visibility(ProjectVisibility.PUBLIC)
                .children(new ArrayList<>(List.of(firstProjectChildren)))
                .build();

        Project secondFirstProjectChildren = Project.builder()
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        Project secondSecondProjectFirstChildren = Project.builder()
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        Project secondSecondProjectChildren = Project.builder()
                .visibility(ProjectVisibility.PUBLIC)
                .children(new ArrayList<>(List.of(secondSecondProjectFirstChildren)))
                .build();

        Project secondChildren = Project.builder()
                .visibility(ProjectVisibility.PUBLIC)
                .children(new ArrayList<>(List.of(secondFirstProjectChildren, secondSecondProjectChildren)))
                .build();

        Project returnProject = Project.builder()
                .name("Faang")
                .updatedAt(LocalDateTime.now())
                .children(new ArrayList<>(List.of(firstChildren, secondChildren)))
                .build();

        when(projectJpaRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(returnProject));

        LocalDateTime result = projectService.updateSubProject(updateSubProjectDto);

        ProjectVisibility firstChildrenVisibility = returnProject.getChildren().get(0).getVisibility();
        ProjectVisibility secondChildrenVisibility = returnProject.getChildren().get(1).getVisibility();

        assertTrue(returnProject.getUpdatedAt().isEqual(result));

        assertSame(returnProject.getVisibility(), ProjectVisibility.PRIVATE);
        assertSame(firstChildrenVisibility, ProjectVisibility.PRIVATE);
        assertSame(secondChildrenVisibility, ProjectVisibility.PRIVATE);
    }

    @Test
    void getProjectChildrenWithFilterTest() {
        ProjectFilterDto projectFilterDto = ProjectFilterDto.builder()
                .status(ProjectStatus.IN_PROGRESS)
                .projectNamePattern("name")
                .build();
        Project first = Project.builder()
                .id(1L)
                .name("My first name")
                .description("Some description")
                .ownerId(10L)
                .parentProject(onlyWithIdProject)
                .children(List.of(onlyWithIdProject))
                .status(ProjectStatus.COMPLETED)
                .visibility(ProjectVisibility.PRIVATE)
                .build();
        Project second = Project.builder()
                .id(2L)
                .name("My second name")
                .description("Some description")
                .ownerId(20L)
                .parentProject(onlyWithIdProject)
                .children(List.of(onlyWithIdProject))
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        List<Project> childrenList = new ArrayList<>(List.of(first, second));

        Project mainProject = Project.builder()
                .children(childrenList)
                .build();

        SubProjectDto secondDto = subProjectMapper.toDto(second);

        List<SubProjectDto> expected = new ArrayList<>(List.of(secondDto));

        Mockito.when(projectJpaRepository.findById(10L))
                .thenReturn(Optional.of(mainProject));

        List<SubProjectDto> result = projectService.getProjectChildrenWithFilter(projectFilterDto, 10L);

        assertEquals(expected, result);
        assertEquals(1, result.size());
    }
}