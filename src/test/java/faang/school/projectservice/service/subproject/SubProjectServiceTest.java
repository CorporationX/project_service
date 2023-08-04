package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.subproject.StatusSubprojectUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
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
    private ProjectRepository projectRepository;
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
        idCompleted = 2L;

        updateStatusSubprojectDto = StatusSubprojectUpdateDto.builder()
                .id(rightId)
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        updateStatusSubprojectDtoCOMPLETED = StatusSubprojectUpdateDto.builder()
                .id(idCompleted)
                .status(ProjectStatus.COMPLETED)
                .build();

        Mockito.when(projectRepository.getProjectById(rightId))
                .thenReturn(project);
    }

    @Test
    public void testUpdateStatusSubProject_True() {
        Project childCompiled = Project.builder()
                .status(ProjectStatus.COMPLETED)
                .build();
        projectCompleted.setChildren(List.of(childCompiled));

        Mockito.when(projectRepository.getProjectById(idCompleted))
                .thenReturn(projectCompleted);

        assertDoesNotThrow(() -> subProjectService.updateStatusSubProject(updateStatusSubprojectDto));
        assertDoesNotThrow(() -> subProjectService.updateStatusSubProject(updateStatusSubprojectDtoCOMPLETED));

        assertEquals(ProjectStatus.COMPLETED, projectCompleted.getStatus());
        assertEquals(ProjectStatus.IN_PROGRESS, project.getStatus());

        assertTrue(project.getUpdatedAt().isBefore(LocalDateTime.now()));
    }

    @Test
    public void testUpdateStatusSubProject_Throw() {
        Project childCreated = Project.builder()
                .status(ProjectStatus.CREATED)
                .build();
        projectCompleted.setChildren(List.of(childCreated));

        Mockito.when(projectRepository.getProjectById(idCompleted))
                .thenReturn(projectCompleted);

        assertThrows(DataValidationException.class,
                () -> subProjectService.updateStatusSubProject(updateStatusSubprojectDtoCOMPLETED));
    }
}