package faang.school.projectservice.service;

import faang.school.projectservice.exception.project.ProjectException;
import faang.school.projectservice.exception.stage.StageException;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StageServiceTest {
    @Mock
    private StageRepository stageRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private StageMapper stageMapper;
    @InjectMocks
    private StageService stageService;

    private Stage stage;
    private Project project;

    @BeforeEach
    public void setUp() {
        project = new Project();
        stage = new Stage();
        stage.setStageId(2L);
        stage.setStageName("stage1");
        stage.setStageRoles(List.of(new StageRoles()));
        stage.setProject(project);
        stage.setTasks(List.of(new Task()));
        project.setId(1L);
    }


    @Test
    public void createStage_CorrectAnswer() {
        project.setStatus(ProjectStatus.CREATED);
        when(projectRepository.getProjectById(anyLong())).thenReturn(project);
        stageService.createStage(stage, project);
        assertEquals(1, project.getStages().size());
    }

    @Test
    public void createStage_ProjectUnavailable() {
        project.setStatus(ProjectStatus.CANCELLED);
        ProjectException projectException = assertThrows(ProjectException.class, () -> stageService.createStage(stage, project));
        assertEquals("Project unavailable", projectException.getMessage());
    }

    @Test
    public void createStage_ValidStage() {
        stage.setStageName("");
        StageException stageException = assertThrows(StageException.class, () -> stageService.createStage(stage, project));
        assertEquals("Stage name cannot be empty", stageException.getMessage());
    }
}