package faang.school.projectservice.service.subproject;


import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.subproject.StatusSubprojectDto;
import faang.school.projectservice.dto.subproject.SubprojectFilterDto;
import faang.school.projectservice.dto.subproject.VisibilitySubprojectDto;
import faang.school.projectservice.filter.subproject.SubprojectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.moment.MomentService;
import faang.school.projectservice.validator.subproject.SubProjectValidator;
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
    private ProjectStatus defaultStatus = ProjectStatus.CREATED;

    @BeforeEach
    public void setUp() {
        rightId = 1L;
        idParent = 3L;
        idCompleted = 2L;

        parentProject = tree.parentProjectA;
        parentProject.setVisibility(ProjectVisibility.PUBLIC);
        projectCompleted.setId(idCompleted);
        projectDto = ProjectDto.builder()
                .id(rightId)
                .parentProjectId(idParent)
                .build();
    }
    @Test
    void testCreateProject() {
        project = tree.projectAEntity;
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
    public void testUpdateStatusSubProject_Not_Completed() {
        project = tree.projectAEntity;
        project.setParentProject(parentProject);
        project.setId(rightId);

        ProjectDto projectDto = ProjectDto.builder().build();
        updateStatusSubprojectDto = StatusSubprojectDto.builder()
                .projectId(rightId)
                .status(defaultStatus)
                .build();

        Mockito.when(projectService.getProjectById(rightId))
                .thenReturn(projectDto);
        Mockito.when(projectMapper.toProject(projectDto))
                .thenReturn(project);

        assertDoesNotThrow(() -> subProjectService.updateStatusSubProject(updateStatusSubprojectDto));
        assertEquals(projectDto.getStatus(), defaultStatus);
        Mockito.verify(subProjectValidator, Mockito.times(1))
                .validateSubProjectStatus(projectDto, defaultStatus);
    }

    @Test
    public void testUpdateStatusSubProject_Completed() {
        projectCompleted.setChildren(List.of(Project.builder()
                .status(ProjectStatus.COMPLETED)
                .visibility(ProjectVisibility.PUBLIC)
                .build()));

        ProjectDto projectCompletedDto = ProjectDto.builder().build();
        updateStatusSubprojectDtoCOMPLETED = StatusSubprojectDto.builder()
                .projectId(idCompleted)
                .status(ProjectStatus.COMPLETED)
                .build();

        Mockito.when(projectService.getProjectById(idCompleted))
                .thenReturn(projectCompletedDto);
        Mockito.when(projectMapper.toProject(projectCompletedDto))
                .thenReturn(projectCompleted);

        assertDoesNotThrow(() -> subProjectService.updateStatusSubProject(updateStatusSubprojectDtoCOMPLETED));
       assertEquals(ProjectStatus.COMPLETED, projectCompletedDto.getStatus());

        Mockito.verify(subProjectValidator, Mockito.times(1))
                .validateSubProjectStatus(projectCompletedDto, ProjectStatus.COMPLETED);
        Mockito.verify(momentMapper, Mockito.times(1))
                .toMomentDto(projectCompleted);
    }

    @Test
    void testUpdateVisibilitySubProjectToPrivate_True() {
        visibilitySubprojectDto = VisibilitySubprojectDto.builder()
                .projectId(rightId)
                .visibility(ProjectVisibility.PRIVATE)
                .build();

        Mockito.when(projectService.getProjectById(visibilitySubprojectDto.getProjectId()))
                        .thenReturn(projectDto);
        Mockito.when(projectService.updateProject(Mockito.anyLong(), Mockito.any(ProjectDto.class)))
                        .thenReturn(projectDto);

        Mockito.when(projectService.getProjectById(rightId))
                        .thenReturn(tree.projectA);
        Mockito.when(projectService.getProjectById(2L))
                .thenReturn(tree.projectB);
        Mockito.when(projectService.getProjectById(3L))
                .thenReturn(tree.projectC);
        Mockito.when(projectService.getProjectById(4L))
                .thenReturn(tree.projectD);
        Mockito.when(projectService.getProjectById(5L))
                .thenReturn(tree.projectE);
        Mockito.when(projectService.getProjectById(6L))
                .thenReturn(tree.projectF);
        Mockito.when(projectService.getProjectById(7L))
                .thenReturn(tree.projectG);

        subProjectService.updateVisibilitySubProject(visibilitySubprojectDto);

        assertEquals(ProjectVisibility.PRIVATE, tree.projectA.getVisibility());
        assertEquals(ProjectVisibility.PRIVATE, tree.projectB.getVisibility());
        assertEquals(ProjectVisibility.PRIVATE, tree.projectC.getVisibility());
        assertEquals(ProjectVisibility.PRIVATE, tree.projectD.getVisibility());
        assertEquals(ProjectVisibility.PRIVATE, tree.projectG.getVisibility());
    }

    @Test
    void testUpdateVisibilitySubProjectToPublic() {
        projectDto.setParentProjectId(idParent);
        ProjectDto parentProjectDto = ProjectDto.builder()
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        visibilitySubprojectDto = VisibilitySubprojectDto.builder()
                .projectId(rightId)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        parentProject.setVisibility(ProjectVisibility.PUBLIC);

        Mockito.when(projectService.getProjectById(rightId))
                .thenReturn(projectDto);
        Mockito.when(projectService.getProjectById(idParent))
                        .thenReturn(parentProjectDto);
        Mockito.when(projectService.updateProject(Mockito.anyLong(), Mockito.any(ProjectDto.class)))
                .thenReturn(projectDto);

        assertDoesNotThrow(() -> subProjectService.updateVisibilitySubProject(visibilitySubprojectDto));
        Mockito.verify(subProjectValidator, Mockito.times(1))
                .validateVisibility(visibilitySubprojectDto.getVisibility(), parentProject.getVisibility());
        assertEquals(ProjectVisibility.PUBLIC, projectDto.getVisibility());
    }

    @Test
    void testPrepareProjectForCreate_NullVisibility() {
        project = tree.projectAEntity;
        project.setParentProject(parentProject);
        project.setId(rightId);

        Mockito.when(projectMapper.toProject(projectService.getProjectById(idParent)))
                .thenReturn(parentProject);

        subProjectService.createSubProject(projectDto);
        assertEquals(parentProject.getVisibility(), project.getVisibility());
    }

    @Test
    void testPrepareProjectForCreate_NotNullVisibility() {
        project = tree.projectAEntity;
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
        Project projectA = Project.builder()
                .status(ProjectStatus.IN_PROGRESS)
                .id(11L)
                .name("Project A")
                .build();
        Project projectB = Project.builder()
                .status(ProjectStatus.COMPLETED)
                .id(12L)
                .name("Project B")
                .build();
        Project projectC = Project.builder()
                .status(ProjectStatus.IN_PROGRESS)
                .id(13L)
                .name("Project C")
                .build();

        ProjectDto parentDto = ProjectDto.builder()
                .childrenIds(List.of(11L, 12L, 13L))
                .build();
        Mockito.when(projectMapper.toProject(projectService.getProjectById(11L)))
                .thenReturn(projectA);
        Mockito.when(projectMapper.toProject(projectService.getProjectById(12L)))
                .thenReturn(projectB);
        Mockito.when(projectMapper.toProject(projectService.getProjectById(13L)))
                .thenReturn(projectC);

        List<SubprojectFilter> subprojectFilters;
        SubprojectFilterDto filter = new SubprojectFilterDto();
        SubprojectFilter subProjectFilter = Mockito.mock(SubprojectFilter.class);
        subprojectFilters = List.of(subProjectFilter);
        filter.setProjectId(rightId);
        SubprojectFilterDto filterProgress = SubprojectFilterDto.builder()
                .projectId(rightId)
                .nameProject("Project A")
                .statusFilter(ProjectStatus.IN_PROGRESS)
                .build();

        subProjectService = new SubProjectService(projectService, momentService, subProjectValidator, projectMapper, momentMapper, new ArrayList<>());
        SubProjectService subProjectServiceMockFilter = new SubProjectService(projectService, momentService, subProjectValidator, projectMapper, momentMapper, subprojectFilters);

        Mockito.when(projectService.getProjectById(rightId))
                .thenReturn(parentDto);
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