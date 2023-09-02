package faang.school.projectservice.service.subproject;


import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.subproject.StatusSubprojectDto;
import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.dto.subproject.SubprojectFilterDto;
import faang.school.projectservice.dto.subproject.VisibilitySubprojectDto;
import faang.school.projectservice.filter.subproject.SubprojectFilter;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.service.moment.MomentService;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.validator.subproject.SubProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;


@ExtendWith(MockitoExtension.class)
class SubProjectServiceTest {
    @InjectMocks
    private SubProjectService subProjectService;
    @Mock
    private MomentService momentService;
    @Mock
    private ProjectService projectService;
    @Mock
    private SubProjectValidator subProjectValidator;
    @Spy
    private ProjectMapper projectMapper;
    @Mock
    private MomentMapper momentMapper;
    private SubProjectDto subProjectDto;
    private StatusSubprojectDto updateStatusSubprojectDtoCOMPLETED;
    private StatusSubprojectDto updateStatusSubprojectDto;
    private Project project;
    private Project projectCompleted = new Project();

    private Long rightId;
    private Long idCompleted;
    private Long idParent;
    private Project parentProject;
    private VisibilitySubprojectDto visibilitySubprojectDto;
    private Tree tree = new Tree();
    private ProjectDto projectDto;

    @BeforeEach
    public void setUp() {
        rightId = 1L;
        idParent = 3L;
        idCompleted = 2L;

        parentProject = tree.parentProjectA;

        parentProject.setVisibility(ProjectVisibility.PUBLIC);

        projectCompleted.setId(idCompleted);

        subProjectDto = subProjectDto.builder()
                .id(rightId)
                .parentProjectId(rightId)
                .build();

        projectDto = ProjectDto.builder()
                .id(rightId)
                .parentProjectId(idParent)
                .build();
    }

    @Test
    public void testUpdateStatusSubProject_Not_Completed() {
        project = tree.projectA;
        project.setParentProject(parentProject);
        project.setId(rightId);

        updateStatusSubprojectDto = StatusSubprojectDto.builder()
                .id(rightId)
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        Mockito.when(projectMapper.toProject(projectService.getProjectById(rightId)))
                .thenReturn(project);

        assertDoesNotThrow(() -> subProjectService.updateStatusSubProject(updateStatusSubprojectDto));

        assertEquals(ProjectStatus.IN_PROGRESS, project.getStatus());

        Mockito.verify(subProjectValidator, Mockito.times(1))
                .validateSubProjectStatus(project.getId());

        assertTrue(project.getUpdatedAt().isBefore(LocalDateTime.now()));
    }

    @Test
    public void testUpdateStatusSubProject_Completed() {
        projectCompleted.setChildren(List.of(Project.builder()
                .status(ProjectStatus.COMPLETED)
                .visibility(ProjectVisibility.PUBLIC)
                .build()));

        updateStatusSubprojectDtoCOMPLETED = StatusSubprojectDto.builder()
                .id(idCompleted)
                .status(ProjectStatus.COMPLETED)
                .build();

        Mockito.when(projectMapper.toProject(projectService.getProjectById(idCompleted)))
                .thenReturn(projectCompleted);

        assertDoesNotThrow(() -> subProjectService.updateStatusSubProject(updateStatusSubprojectDtoCOMPLETED));
        assertEquals(ProjectStatus.COMPLETED, projectCompleted.getStatus());

        Mockito.verify(subProjectValidator, Mockito.times(1))
                .validateSubProjectStatus(projectCompleted.getId());
        Mockito.verify(momentMapper, Mockito.times(1))
                .toMomentDto(projectCompleted);

        assertTrue(projectCompleted.getUpdatedAt().isBefore(LocalDateTime.now()));
    }

    @Test
    void testUpdateVisibilitySubProjectToPrivate_True() {
        project = tree.projectA;
        project.setParentProject(parentProject);
        project.setId(rightId);

        visibilitySubprojectDto = VisibilitySubprojectDto.builder()
                .id(rightId)
                .visibility(ProjectVisibility.PRIVATE)
                .build();

        Mockito.when(projectMapper.toProject(projectService.getProjectById(rightId)))
                .thenReturn(project);

        subProjectService.updateVisibilitySubProject(visibilitySubprojectDto);

        assertEquals(ProjectVisibility.PRIVATE, tree.projectA.getVisibility());
        assertEquals(ProjectVisibility.PRIVATE, tree.projectB.getVisibility());
        assertEquals(ProjectVisibility.PRIVATE, tree.projectC.getVisibility());
        assertEquals(ProjectVisibility.PRIVATE, tree.projectD.getVisibility());
        assertEquals(ProjectVisibility.PRIVATE, tree.projectG.getVisibility());
    }

    @Test
    void testUpdateVisibilitySubProjectToPublic() {
        project = tree.projectA;
        project.setParentProject(parentProject);
        project.setId(rightId);

        visibilitySubprojectDto = VisibilitySubprojectDto.builder()
                .id(rightId)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        project.setVisibility(ProjectVisibility.PRIVATE);
        parentProject.setVisibility(ProjectVisibility.PUBLIC);

        Mockito.when(projectMapper.toProject(projectService.getProjectById(rightId)))
                .thenReturn(project);

        assertDoesNotThrow(() -> subProjectService.updateVisibilitySubProject(visibilitySubprojectDto));
        Mockito.verify(subProjectValidator, Mockito.times(1))
                .validateVisibility(project.getVisibility(), parentProject.getVisibility());
        assertEquals(ProjectVisibility.PUBLIC, project.getVisibility());
    }

    @Test
    void testCreateProject() {
        project = tree.projectA;
        project.setParentProject(parentProject);
        project.setId(rightId);

        Mockito.when(projectMapper.toProject(projectService.getProjectById(rightId)))
                .thenReturn(parentProject);

        subProjectService.createSubProject(projectDto);

        Mockito.verify(subProjectValidator, Mockito.times(1))
                .validateCreateProjectDto(projectDto);
        Mockito.verify(projectService, Mockito.times(1))
                .createProject(projectDto);
    }

    @Test
    void testPrepareProjectForCreate_NullVisibility() {
        project = tree.projectA;
        project.setParentProject(parentProject);
        project.setId(rightId);

        Mockito.when(projectMapper.toProject(projectService.getProjectById(idParent)))
                .thenReturn(parentProject);

        subProjectService.createSubProject(projectDto);
        assertEquals(parentProject.getVisibility(), project.getVisibility());
    }

    @Test
    void testPrepareProjectForCreate_NotNullVisibility() {
        project = tree.projectA;
        project.setParentProject(parentProject);
        project.setId(rightId);

        Mockito.when(projectMapper.toProject(projectService.getProjectById(idParent)))
                .thenReturn(parentProject);

        projectDto.setVisibility(ProjectVisibility.PRIVATE);
        subProjectService.createSubProject(projectDto);
        Mockito.verify(subProjectValidator, Mockito.times(1))
                .validateVisibility(projectDto.getVisibility(), parentProject.getVisibility());
    }

    @Test
    public void testGetSubProject() {
        List<Project> projects = new ArrayList<>();

        Project projectA = Project.builder()
                .status(ProjectStatus.IN_PROGRESS)
                .name("Project A")
                .build();
        Project projectB = Project.builder()
                .status(ProjectStatus.COMPLETED)
                .name("Project B")
                .build();
        Project projectC = Project.builder()
                .status(ProjectStatus.IN_PROGRESS)
                .name("Project C")
                .build();
        Project parent = new Project();

        projects.add(projectA);
        projects.add(projectB);
        projects.add(projectC);

        parent.setChildren(projects);

        List<SubprojectFilter> subprojectFilters;
        SubprojectFilterDto filter = new SubprojectFilterDto();
        SubprojectFilter subProjectFilter = Mockito.mock(SubprojectFilter.class);
        subprojectFilters = List.of(subProjectFilter);
        filter.setId(rightId);
        SubprojectFilterDto filterProgress = SubprojectFilterDto.builder()
                .id(rightId)
                .nameFilter("Project A")
                .statusFilter(ProjectStatus.IN_PROGRESS)
                .build();

        subProjectService = new SubProjectService(projectService, momentService, subProjectValidator, projectMapper, momentMapper, new ArrayList<>());
        SubProjectService subProjectServiceMockFilter = new SubProjectService(projectService, momentService, subProjectValidator, projectMapper, momentMapper, subprojectFilters);

        Mockito.when(projectMapper.toProject(projectService.getProjectById(idParent)))
                .thenReturn(parent);
        Mockito.when(subprojectFilters.get(0).isApplicable(filterProgress))
                .thenReturn(true);
        Mockito.when(subprojectFilters.get(0).apply(any(), any()))
                .thenReturn(List.of(projectA).stream());

        subProjectService.getAllSubProject(filterProgress);
        Mockito.verify(projectService, Mockito.times(1))
                .getProjectById(rightId);

        assertEquals(1, subProjectServiceMockFilter.getAllSubProject(filterProgress).size());
        assertEquals(0, subProjectService.getAllSubProject(filterProgress).size());
    }
}