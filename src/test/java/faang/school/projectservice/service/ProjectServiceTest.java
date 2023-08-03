package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.SubProjectMapper;
import faang.school.projectservice.mapper.SubProjectMapperImpl;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.filters.ProjectFilter;
import faang.school.projectservice.service.filters.ProjectFilterByName;
import faang.school.projectservice.service.filters.ProjectFilterByStatus;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;
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
    @Mock
    private MomentRepository momentRepository;
    @InjectMocks
    private ProjectService projectService;
    private ProjectDto projectDto;
    private ProjectDto updatedProjectDto;
    private Project project;
    private Project onlyWithIdProject;
    private List<Project> children;
    private List<Moment> moments;

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
        this.updatedProjectDto = ProjectDto.builder()
                .id(1L)
                .status(ProjectStatus.COMPLETED)
                .childrenIds(Collections.emptyList())
                .build();
        this.project = Project.builder()
                .id(2L)
                .visibility(ProjectVisibility.PUBLIC)
                .children(new ArrayList<>())
                .build();
        this.onlyWithIdProject = Project.builder()
                .id(1L)
                .build();
        this.children = List.of(Project.builder()
                .id(10L)
                .build());
        this.moments = List.of(Moment.builder()
                .userIds(List.of(1L))
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
                .ownerId(1)
                .build();

        DataValidationException exception = assertThrows(DataValidationException.class, () -> projectService.createSubProject(wrongProjectDto));

        assertEquals("Subprojects can be empty but not null", exception.getMessage());
    }

    @Test
    void createSubProjectThrowExceptionWhenStatusNull() {
        ProjectDto wrongProjectDto = ProjectDto.builder()
                .ownerId(1)
                .childrenIds(Collections.emptyList())
                .build();

        DataValidationException exception = assertThrows(DataValidationException.class, () -> projectService.createSubProject(wrongProjectDto));

        assertEquals("Project status cant be null", exception.getMessage());
    }

    @Test
    void createSubProjectThrowExceptionWhenVisibilityNull() {
        ProjectDto wrongProjectDto = ProjectDto.builder()
                .name("Faang")
                .ownerId(1)
                .childrenIds(Collections.emptyList())
                .status(ProjectStatus.CREATED)
                .build();

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
                .status(ProjectStatus.CREATED)
                .build();

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
                .status(ProjectStatus.CREATED)
                .build();

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> projectService.createSubProject(wrongProjectDto));

        assertEquals("Parent project not found by id: 100", exception.getMessage());
    }

    @Test
    void createSubProjectThrowExceptionWhenTryingToCreatePrivateSubProjectOnAPublicProject() {
        ProjectDto wrongProjectDto = ProjectDto.builder()
                .name("Faang")
                .ownerId(1)
                .parentProjectId(2L)
                .childrenIds(Collections.emptyList())
                .visibility(ProjectVisibility.PRIVATE)
                .status(ProjectStatus.CREATED)
                .build();

        Mockito.when(projectRepository.getProjectById(wrongProjectDto.getParentProjectId()))
                .thenReturn(Project.builder()
                        .name("Uber")
                        .visibility(ProjectVisibility.PUBLIC)
                        .build());

        DataValidationException exception = assertThrows(DataValidationException.class, () -> projectService.createSubProject(wrongProjectDto));

        assertEquals("Private SubProject; Faang, cant be with a public Parent Project: Uber", exception.getMessage());
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

        Mockito.verify(projectRepository, Mockito.times(3)).getProjectById(projectDto.getParentProjectId());
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

    @Test
    void updateSubProjectThrowExceptionWhenChildrenStatusesAreNotComplete() {
        ProjectDto fakeProject = ProjectDto.builder()
                .status(ProjectStatus.COMPLETED)
                .childrenIds(Collections.emptyList())
                .build();

        Mockito.when(projectRepository.findAllByIds(fakeProject.getChildrenIds()))
                .thenReturn(List.of(Project.builder()
                        .status(ProjectStatus.IN_PROGRESS)
                        .build()));

        DataValidationException exception = assertThrows(DataValidationException.class, () -> projectService.updateSubProject(fakeProject));

        assertEquals("Can't close project if subProject status are not complete or cancelled", exception.getMessage());
    }

    @Test
    void updateSubProjectInvokesGetProjectByIdAndFindAllByIds() {
        updatedProjectDto.setOwnerId(2L);
        updatedProjectDto.setParentProjectId(3L);
        Mockito.when(projectRepository.getProjectById(updatedProjectDto.getId()))
                .thenReturn(Project.builder()
                        .parentProject(Project.builder().id(2L).build())
                        .updatedAt(LocalDateTime.now())
                        .children(List.of(onlyWithIdProject))
                        .build());
        Mockito.when(projectRepository.findAllByIds(updatedProjectDto.getChildrenIds()))
                .thenReturn(List.of(Project.builder()
                        .id(5L)
                        .status(ProjectStatus.COMPLETED)
                        .build()));
        Mockito.when(momentRepository.findAllByProjectId(Mockito.anyLong()))
                .thenReturn(moments);

        projectService.updateSubProject(updatedProjectDto);

        Mockito.verify(projectRepository).getProjectById(updatedProjectDto.getId());
        Mockito.verify(projectRepository).findAllByIds(updatedProjectDto.getChildrenIds());
    }

    @Test
    void updateSubProjectInvokesSaveMethods() {
        updatedProjectDto.setOwnerId(2L);
        Project test = Project.builder()
                .parentProject(Project.builder().id(2L).build())
                .updatedAt(LocalDateTime.now())
                .children(List.of(onlyWithIdProject))
                .build();
        Project projectToSave = Project.builder()
                .ownerId(2L)
                .status(ProjectStatus.COMPLETED)
                .children(List.of(Project.builder()
                        .id(5L)
                        .status(ProjectStatus.COMPLETED)
                        .build()))
                .updatedAt(test.getUpdatedAt())
                .build();
        Moment moment = Moment.builder()
                .name(String.format("%s project tasks", updatedProjectDto.getName()))
                .description(String.format("All tasks are completed in %s project", updatedProjectDto.getName()))
                .projects(List.of(Project.builder().
                                id(5L).
                                status(ProjectStatus.COMPLETED)
                                .build(),
                        Project.builder()
                                .ownerId(2L)
                                .children(List.of(Project.builder().
                                        id(5L).
                                        status(ProjectStatus.COMPLETED)
                                        .build()))
                                .updatedAt(test.getUpdatedAt())
                                .status(ProjectStatus.COMPLETED)
                                .build()))
                .createdBy(2L)
                .updatedBy(2L)
                .build();
        moment.setUserIds(List.of(1L));
        onlyWithIdProject.setStatus(ProjectStatus.COMPLETED);

        Mockito.when(projectRepository.getProjectById(updatedProjectDto.getId()))
                .thenReturn(test);
        Mockito.when(projectRepository.findAllByIds(updatedProjectDto.getChildrenIds()))
                .thenReturn(List.of(Project.builder()
                        .id(5L)
                        .status(ProjectStatus.COMPLETED)
                        .build()));
        Mockito.when(momentRepository.findAllByProjectId(Mockito.anyLong()))
                .thenReturn(moments);

        projectService.updateSubProject(updatedProjectDto);

        Mockito.verify(projectRepository).save(projectToSave);
        Mockito.verify(momentRepository).save(moment);
    }

    @Test
    void updateSubProjectTest() {
        project.setParentProject(Project.builder().id(2L).build());
        project.setVisibility(ProjectVisibility.PRIVATE);
        project.setUpdatedAt(LocalDateTime.now().minusMonths(1));
        projectDto.setVisibility(ProjectVisibility.PRIVATE);
        projectDto.setParentProjectId(null);

        Mockito.when(projectRepository.getProjectById(projectDto.getId()))
                .thenReturn(project);
        Mockito.when(projectRepository.findAllByIds(projectDto.getChildrenIds()))
                .thenReturn(children);

        Timestamp result = projectService.updateSubProject(projectDto);

        assertEquals(Timestamp.valueOf(project.getUpdatedAt()), result);
    }

    @Test
    void createMomentTest() {
        projectDto.setId(1L);
        Moment expected = Moment.builder()
                .name("Faang project tasks")
                .description("All tasks are completed in Faang project")
                .projects(new ArrayList<>(List.of(project)))
                .userIds(Collections.emptyList())
                .createdBy(1L)
                .build();

        Moment result = projectService.createMoment(projectDto, project);

        assertEquals(expected, result);
    }

    @Test
    void collectAllUsersIdOnProjectWhenOnlyTeamExistTest() {
        TeamMember first = TeamMember.builder()
                .id(5L)
                .build();
        TeamMember second = TeamMember.builder()
                .id(10L)
                .build();
        Team team = Team.builder()
                .teamMembers(List.of(first, second))
                .build();
        Project projectWithTeam = Project.builder()
                .teams(List.of(team))
                .build();

        List<Long> expected = List.of(5L, 10L);

        List<Long> result = projectService.collectAllUsersIdOnProject(projectWithTeam);

        assertEquals(expected, result);
        assertEquals(2, result.size());
    }

    @Test
    void collectAllUsersIdOnProjectTestWhenOnlyChildrenExist() {
        Project projectWithChildren = Project.builder()
                .children(List.of(onlyWithIdProject, onlyWithIdProject))
                .build();
        List<Long> expected = List.of(1L);


        Mockito.when(momentRepository.findAllByProjectId(1L))
                .thenReturn(moments);

        List<Long> result = projectService.collectAllUsersIdOnProject(projectWithChildren);

        assertEquals(expected, result);
        assertEquals(1, result.size());

        Mockito.verify(momentRepository, Mockito.times(2)).findAllByProjectId(1L);
    }

    @Test
    void changeParentProjectTest() {
        Project originalProject = Project.builder()
                .parentProject(Project.builder().id(5L).build())
                .build();

        Project getProjectById = Project.builder()
                .children(new ArrayList<>())
                .build();

        Project expected = Project.builder()
                .parentProject(Project.builder()
                        .children(new ArrayList<>())
                        .build())
                .build();

        Mockito.when(projectRepository.getProjectById(2L))
                .thenReturn(getProjectById);

        Project result = projectService.changeParentProject(projectDto, originalProject);

        assertEquals(expected, result);
        Mockito.verify(projectRepository).getProjectById(2L);
    }

    @Test
    void getProjectChildrenWithFilterTest() {
        List<ProjectFilter> projectFilters = new ArrayList<>(List.of(new ProjectFilterByName(), new ProjectFilterByStatus()));
        ProjectService mockedProjectService = ProjectService.builder()
                .projectRepository(projectRepository)
                .subProjectMapper(subProjectMapper)
                .stageRepository(stageRepository)
                .momentRepository(momentRepository)
                .projectFilters(projectFilters)
                .build();
        long projectId = 10L;
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
                .stages(List.of(Stage.builder().stageId(15L).build()))
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
                .stages(List.of(Stage.builder().stageId(15L).build()))
                .build();
        List<Project> childrenList = new ArrayList<>(List.of(first, second));

        Project mainProject = Project.builder()
                .children(childrenList)
                .build();

        ProjectDto secondDto = subProjectMapper.toDto(second);

        List<ProjectDto> expected = new ArrayList<>(List.of(secondDto));

        Mockito.when(projectRepository.getProjectById(projectId)).thenReturn(mainProject);

        List<ProjectDto> result = mockedProjectService.getProjectChildrenWithFilter(projectFilterDto, projectId);

        assertEquals(expected, result);
        assertEquals(1, result.size());
    }
}