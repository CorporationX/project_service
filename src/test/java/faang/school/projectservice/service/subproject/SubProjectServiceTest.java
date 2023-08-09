package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.subproject.StatusSubprojectUpdateDto;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
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
    private StatusSubprojectUpdateDto updateStatusSubprojectDtoCOMPLETED;
    private StatusSubprojectUpdateDto updateStatusSubprojectDto;
    private Project project = new Project();
    private Project projectCompleted = new Project();

    private Long rightId;
    private Long idCompleted;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        rightId = 1L;
        project.setId(rightId);
        idCompleted = 2L;
        projectCompleted.setId(rightId);

        updateStatusSubprojectDto = StatusSubprojectUpdateDto.builder()
                .id(rightId)
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        updateStatusSubprojectDtoCOMPLETED = StatusSubprojectUpdateDto.builder()
                .id(idCompleted)
                .status(ProjectStatus.COMPLETED)
                .build();

        Mockito.when(projectService.getProjectById(rightId))
                .thenReturn(project);
    }

    @Test
    public void testUpdateStatusSubProject_True() {
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

        assertTrue(project.getUpdatedAt().isBefore(LocalDateTime.now()));
    }
}