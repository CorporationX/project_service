package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.SubProjectMapper;
import faang.school.projectservice.mapper.SubProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;
    @Spy
    private SubProjectMapper subProjectMapper = new SubProjectMapperImpl();
    @Mock
    private StageRepository stageRepository;
    @InjectMocks
    private ProjectService projectService;
    private ProjectDto projectDto;
    private Project project;
    private List<Project> children;

    @BeforeEach
    void setUp() {

        this.projectDto = ProjectDto.builder()
                .name("Faang")
                .description("This is Faang")
                .ownerId(1)
                .parentProjectId(2L)
                .childrenIds(List.of(10L))
                .visibility(ProjectVisibility.PUBLIC)
                .stagesId(Collections.emptyList())
                .status(ProjectStatus.CREATED)
                .build();
        this.project = Project.builder()
                .id(2L)
                .visibility(ProjectVisibility.PUBLIC)
                .children(new ArrayList<>())
                .build();
        this.children = List.of(Project.builder()
                .id(10L)
                .build());
    }

    @Test
    void createSubProjectThrowExceptionWhenOwnerIdLessThenOne() {
        ProjectDto projectDto = ProjectDto.builder()
                .name("Faang")
                .ownerId(0)
                .parentProjectId(2L)
                .build();

        DataValidationException exception = assertThrows(DataValidationException.class, () -> projectService.createSubProject(projectDto));

        assertEquals("Owner id cant be less then 1", exception.getMessage());
    }

    @Test
    void createSubProjectThrowExceptionWhenProjectIsAlreadyExist() {
        Mockito.when(projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName()))
                .thenReturn(true);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> projectService.createSubProject(projectDto));

        assertEquals("Project Faang is already exist", exception.getMessage());
    }

    @Test
    void createSubProjectThrowExceptionWhenChildrenNull() {
        ProjectDto wrongProjectDto = ProjectDto.builder()
                .name("Faang")
                .ownerId(1)
                .parentProjectId(2L)
                .build();

        Mockito.when(projectRepository.existsByOwnerUserIdAndName(wrongProjectDto.getOwnerId(), wrongProjectDto.getName()))
                .thenReturn(false);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> projectService.createSubProject(wrongProjectDto));

        assertEquals("Subprojects can be empty but not null", exception.getMessage());
    }

    @Test
    void createSubProjectThrowExceptionWhenVisibilityNull() {
        ProjectDto wrongProjectDto = ProjectDto.builder()
                .name("Faang")
                .ownerId(1)
                .parentProjectId(2L)
                .childrenIds(Collections.emptyList())
                .build();

        Mockito.when(projectRepository.existsByOwnerUserIdAndName(wrongProjectDto.getOwnerId(), wrongProjectDto.getName()))
                .thenReturn(false);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> projectService.createSubProject(wrongProjectDto));

        assertEquals("Visibility of subProject 'Faang' must be specified as 'private' or 'public'.", exception.getMessage());
    }

    @Test
    void createSubProjectThrowExceptionWhenParentProjectIdLessThenOne() {
        ProjectDto wrongProjectDto = ProjectDto.builder()
                .name("Faang")
                .ownerId(1)
                .parentProjectId(-2L)
                .visibility(ProjectVisibility.PUBLIC)
                .childrenIds(Collections.emptyList())
                .build();

        Mockito.when(projectRepository.existsByOwnerUserIdAndName(wrongProjectDto.getOwnerId(), wrongProjectDto.getName()))
                .thenReturn(false);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> projectService.createSubProject(wrongProjectDto));

        assertEquals("ParentProjectId cant be less 0 or 0", exception.getMessage());
    }

    @Test
    void createSubProjectThrowEntityNotFoundException() {
        ProjectDto wrongProjectDto = ProjectDto.builder()
                .name("Faang")
                .ownerId(1)
                .parentProjectId(100L)
                .childrenIds(Collections.emptyList())
                .visibility(ProjectVisibility.PRIVATE)
                .build();

        Mockito.when(projectRepository.existsByOwnerUserIdAndName(wrongProjectDto.getOwnerId(), wrongProjectDto.getName()))
                .thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> projectService.createSubProject(wrongProjectDto));

        assertEquals("Project not found by id: 100", exception.getMessage());
    }

    @Test
    void createSubProjectThrowExceptionWhenTryingToCreatePrivateSubProjectOnAPublicProject() {
        ProjectDto wrongProjectDto = ProjectDto.builder()
                .name("Faang")
                .ownerId(1)
                .parentProjectId(2L)
                .childrenIds(Collections.emptyList())
                .visibility(ProjectVisibility.PRIVATE)
                .build();

        Mockito.when(projectRepository.existsByOwnerUserIdAndName(wrongProjectDto.getOwnerId(), wrongProjectDto.getName()))
                .thenReturn(false);
        Mockito.when(projectRepository.getProjectById(wrongProjectDto.getParentProjectId()))
                .thenReturn(Project.builder()
                        .name("Uber")
                        .visibility(ProjectVisibility.PUBLIC)
                        .build());

        DataValidationException exception = assertThrows(DataValidationException.class, () -> projectService.createSubProject(wrongProjectDto));

        assertEquals("Cant create private SubProject; Faang, on a public Project: Uber", exception.getMessage());
    }

    @Test
    void createSubProjectTest() {
        Mockito.when(projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName()))
                .thenReturn(false);
        Mockito.when(projectRepository.getProjectById(projectDto.getParentProjectId()))
                .thenReturn(project);
        Mockito.when(projectRepository.findAllByIds(projectDto.getChildrenIds()))
                .thenReturn(children);

        ProjectDto result = projectService.createSubProject(projectDto);

        assertEquals(projectDto, result);
    }

    @Test
    void createSubProjectInvokesFindAllByIds() {
        Mockito.when(projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName()))
                .thenReturn(false);
        Mockito.when(projectRepository.getProjectById(projectDto.getParentProjectId()))
                .thenReturn(project);
        Mockito.when(projectRepository.findAllByIds(projectDto.getChildrenIds()))
                .thenReturn(children);

        projectService.createSubProject(projectDto);

        Mockito.verify(projectRepository).findAllByIds(projectDto.getChildrenIds());
    }

    @Test
    void createSubProjectInvokesFindProjectById() {
        Mockito.when(projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName()))
                .thenReturn(false);
        Mockito.when(projectRepository.getProjectById(projectDto.getParentProjectId()))
                .thenReturn(project);
        Mockito.when(projectRepository.findAllByIds(projectDto.getChildrenIds()))
                .thenReturn(children);

        projectService.createSubProject(projectDto);

        Mockito.verify(projectRepository, Mockito.times(2)).getProjectById(projectDto.getParentProjectId());
    }

    @Test
    void createSubProjectInvokesSave() {
        Mockito.when(projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName()))
                .thenReturn(false);
        Mockito.when(projectRepository.getProjectById(projectDto.getParentProjectId()))
                .thenReturn(project);
        Mockito.when(projectRepository.findAllByIds(projectDto.getChildrenIds()))
                .thenReturn(children);

        projectService.createSubProject(projectDto);

        Mockito.verify(projectRepository, Mockito.times(2)).save(Mockito.any());
    }

    @Test
    void createSubProjectAddSubProjectToParentProjectChildrenList() {
        Mockito.when(projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName()))
                .thenReturn(false);
        Mockito.when(projectRepository.getProjectById(projectDto.getParentProjectId()))
                .thenReturn(project);
        Mockito.when(projectRepository.findAllByIds(projectDto.getChildrenIds()))
                .thenReturn(children);

        projectService.createSubProject(projectDto);

        assertEquals(1, project.getChildren().size());
    }

    @Test
    void createSubProjects() {
        List<ProjectDto> expected = new ArrayList<>();
        expected.add(projectDto);
        expected.add(projectDto);

        Mockito.when(projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName()))
                .thenReturn(false);
        Mockito.when(projectRepository.getProjectById(projectDto.getParentProjectId()))
                .thenReturn(project);
        Mockito.when(projectRepository.findAllByIds(projectDto.getChildrenIds()))
                .thenReturn(children);

        List<ProjectDto> result = projectService.createSubProjects(expected);

        assertEquals(expected, result);
        assertEquals(2, result.size());
    }
}