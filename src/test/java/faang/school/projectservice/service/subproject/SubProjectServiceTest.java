package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.subproject.StatusSubprojectDto;
import faang.school.projectservice.dto.subproject.SubprojectFilterDto;
import faang.school.projectservice.filter.subproject.SubprojectFilter;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.service.project.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;


@ExtendWith(MockitoExtension.class)
class SubProjectServiceTest {
    @Mock
    private ProjectService projectService;
    @Mock
    private ProjectMapper projectMapper;
    @InjectMocks
    private SubProjectService subProjectService;
    private SubProjectService subProjectServiceMockFilter;

    private List<SubprojectFilter> subprojectFilters;
    private SubprojectFilterDto filter = new SubprojectFilterDto();
    List<Project> projects = new ArrayList<>();
    private long rightId;
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
    private Project parent = new Project();

    @BeforeEach
    public void setUp() {
        rightId = 1L;

        filter.setId(rightId);

        projects.add(projectA);
        projects.add(projectB);
        projects.add(projectC);

        parent.setChildren(projects);

        SubprojectFilter subProjectFilter = Mockito.mock(SubprojectFilter.class);

        subprojectFilters = List.of(subProjectFilter);

        subProjectService = new SubProjectService(projectService, new ArrayList<>(), projectMapper);
        subProjectServiceMockFilter = new SubProjectService(projectService, subprojectFilters, projectMapper);

        Mockito.when(projectService.getProjectById(rightId))
                .thenReturn(parent);

        rightId = 1L;
        idParent = 3L;
        idCompleted = 2L;

        project = tree.projectA;
        parentProject = tree.parentProjectA;

        parentProject.setVisibility(ProjectVisibility.PUBLIC);

        project.setParentProject(parentProject);
        project.setId(rightId);
        projectCompleted.setId(rightId);

        updateStatusSubprojectDto = StatusSubprojectDto.builder()
                .id(rightId)
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        updateStatusSubprojectDtoCOMPLETED = StatusSubprojectDto.builder()
                .id(idCompleted)
                .status(ProjectStatus.COMPLETED)
                .build();

        subProjectDto = subProjectDto.builder()
                .id(rightId)
                .description("Disc")
                .name("Name")
                .ownerId(rightId)
                .parentProjectId(rightId)
                .build();

        projectDto = ProjectDto.builder()
                .id(rightId)
                .parentProjectId(idParent)
                .build();

        Mockito.when(projectService.getProjectById(rightId))
                .thenReturn(project);
        Mockito.when(projectService.getProjectById(idParent))
                .thenReturn(parentProject);
    }


import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.subproject.StatusSubprojectDto;
import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.dto.subproject.VisibilitySubprojectDto;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.service.moment.MomentService;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.validator.subproject.SubProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubProjectServiceTest {
    @InjectMocks
    private SubProjectService subProjectService;
    @Mock
    private MomentService momentService;
    @Mock
    private ProjectService projectService;
    @Mock
    private SubProjectValidator subProjectValidator;
    @Mock
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
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rightId = 1L;
        idParent = 3L;
        idCompleted = 2L;

        project = tree.projectA;
        parentProject = tree.parentProjectA;

        parentProject.setVisibility(ProjectVisibility.PUBLIC);

        project.setParentProject(parentProject);
        project.setId(rightId);
        projectCompleted.setId(rightId);

        updateStatusSubprojectDto = StatusSubprojectDto.builder()
                .id(rightId)
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        updateStatusSubprojectDtoCOMPLETED = StatusSubprojectDto.builder()
                .id(idCompleted)
                .status(ProjectStatus.COMPLETED)
                .build();

        subProjectDto = subProjectDto.builder()
                .id(rightId)
                .description("Disc")
                .name("Name")
                .ownerId(rightId)
                .parentProjectId(rightId)
                .build();

        projectDto = ProjectDto.builder()
                .id(rightId)
                .parentProjectId(idParent)
                .build();

        Mockito.when(projectService.getProjectById(rightId))
                .thenReturn(project);
        Mockito.when(projectService.getProjectById(idParent))
                .thenReturn(parentProject);
    }

    @Test
    public void testUpdateStatusSubProject() {
        Project childCompiled = Project.builder()
                .status(ProjectStatus.COMPLETED)
                .build();
        projectCompleted.setChildren(List.of(childCompiled));

        Mockito.when(projectService.getProjectById(idCompleted))
                .thenReturn(projectCompleted);

        assertDoesNotThrow(() -> subProjectService.updateStatusSubProject(updateStatusSubprojectDto));
        assertDoesNotThrow(() -> subProjectService.updateStatusSubProject(updateStatusSubprojectDtoCOMPLETED));

        assertEquals(ProjectStatus.COMPLETED, projectCompleted.getStatus());
        assertEquals(ProjectStatus.IN_PROGRESS, project.getStatus());

        Mockito.verify(subProjectValidator, Mockito.times(1))
                .validateSubProjectStatus(project.getId());
        Mockito.verify(momentMapper, Mockito.times(1))
                .toMomentDto(projectCompleted);

        assertTrue(project.getUpdatedAt().isBefore(LocalDateTime.now()));
    }

    @Test
    void testUpdateVisibilitySubProjectToPrivate_True() {
        visibilitySubprojectDto = VisibilitySubprojectDto.builder()
                .id(rightId)
                .visibility(ProjectVisibility.PRIVATE)
                .build();

        Mockito.when(projectService.getProjectById(rightId))
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
        visibilitySubprojectDto = VisibilitySubprojectDto.builder()
                .id(rightId)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        project.setVisibility(ProjectVisibility.PRIVATE);
        parentProject.setVisibility(ProjectVisibility.PUBLIC);

        Mockito.when(projectService.getProjectById(rightId))
                .thenReturn(project);

        assertDoesNotThrow(() -> subProjectService.updateVisibilitySubProject(visibilitySubprojectDto));
        Mockito.verify(subProjectValidator, Mockito.times(1))
                .validateVisibility(project.getVisibility(), parentProject.getVisibility());
        assertEquals(ProjectVisibility.PUBLIC, project.getVisibility());
    }

    @Test
    void testCreateProject() {
        subProjectService.createSubProject(projectDto);

        Mockito.verify(subProjectValidator, Mockito.times(1))
                .validateCreateProjectDto(projectDto);
        Mockito.verify(projectService, Mockito.times(1))
                .createProject(projectDto);
    }

    @Test
    void testPrepareProjectForCreate_NullVisibility() {
        subProjectService.createSubProject(projectDto);
        assertEquals(parentProject.getVisibility(), project.getVisibility());
    }

    @Test
    void testPrepareProjectForCreate_NotNullVisibility() {
        projectDto.setVisibility(ProjectVisibility.PRIVATE);
        subProjectService.createSubProject(projectDto);
        Mockito.verify(subProjectValidator, Mockito.times(1))
                .validateVisibility(projectDto.getVisibility(), parentProject.getVisibility());
    }

    @Test
    public void testGetSubProject() {
        SubprojectFilterDto filterProgress = SubprojectFilterDto.builder()
                .id(rightId)
                .nameFilter("Project A")
                .statusFilter(ProjectStatus.IN_PROGRESS)
                .build();

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