package faang.school.projectservice.service.subproject;

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

}