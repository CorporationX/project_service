package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.subproject.StatusSubprojectDto;
import faang.school.projectservice.dto.subproject.VisibilitySubprojectUpdateDto;
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
    private StatusSubprojectDto updateStatusSubprojectDtoCOMPLETED;
    private StatusSubprojectDto updateStatusSubprojectDto;
    private Project project;
    private Project projectCompleted = new Project();

    private Long rightId;
    private Long idCompleted;
    private Project parentProject;
    private VisibilitySubprojectUpdateDto visibilitySubprojectUpdateDto;
    private Tree tree = new Tree();
    private Long projectId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        project = tree.projectA;
        parentProject = tree.parentProjectA;
        projectId = 1L;

        project.setParentProject(parentProject);
        rightId = 1L;
        project.setId(rightId);
        idCompleted = 2L;
        projectCompleted.setId(rightId);

        updateStatusSubprojectDto = StatusSubprojectDto.builder()
                .id(rightId)
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        updateStatusSubprojectDtoCOMPLETED = StatusSubprojectDto.builder()
                .id(idCompleted)
                .status(ProjectStatus.COMPLETED)
                .build();

        Mockito.when(projectService.getProjectById(rightId))
                .thenReturn(project);
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
        Mockito.verify(momentMapper,Mockito.times(1))
                        .toMomentDto(projectCompleted);

        assertTrue(project.getUpdatedAt().isBefore(LocalDateTime.now()));
    }

    @Test
    void testUpdateVisibilitySubProjectToPrivate_True() {
        visibilitySubprojectUpdateDto = VisibilitySubprojectUpdateDto.builder()
                .id(projectId)
                .visibility(ProjectVisibility.PRIVATE)
                .build();

        Mockito.when(projectService.getProjectById(projectId))
                .thenReturn(project);

        subProjectService.updateVisibilitySubProject(visibilitySubprojectUpdateDto);

        assertEquals(ProjectVisibility.PRIVATE, tree.projectA.getVisibility());
        assertEquals(ProjectVisibility.PRIVATE, tree.projectB.getVisibility());
        assertEquals(ProjectVisibility.PRIVATE, tree.projectC.getVisibility());
        assertEquals(ProjectVisibility.PRIVATE, tree.projectD.getVisibility());
        assertEquals(ProjectVisibility.PRIVATE, tree.projectG.getVisibility());
    }

    @Test
    void testUpdateVisibilitySubProjectToPublic() {
        visibilitySubprojectUpdateDto = VisibilitySubprojectUpdateDto.builder()
                .id(projectId)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        project.setVisibility(ProjectVisibility.PRIVATE);
        parentProject.setVisibility(ProjectVisibility.PUBLIC);

        Mockito.when(projectService.getProjectById(projectId))
                .thenReturn(project);

        assertDoesNotThrow(() -> subProjectService.updateVisibilitySubProject(visibilitySubprojectUpdateDto));
        Mockito.verify(subProjectValidator, Mockito.times(1))
                        .validateVisibility(project.getVisibility(), parentProject.getVisibility());
        assertEquals(ProjectVisibility.PUBLIC, project.getVisibility());
    }
}