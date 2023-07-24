package faang.school.projectservice.service;


import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.stage.StageMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class StageServiceTest {
    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private StageRepository stageRepository;

    @InjectMocks
    private StageService stageService;
    @Spy
    private StageMapperImpl stageMapper;

    private Stage stage;

    @BeforeEach
    void init(){
        stage = Stage.builder()
                .stageId(1L)
                .stageName("stage")

                .project(Project.builder().id(1L).status(ProjectStatus.CANCELLED).build())
                .build();
    }

    @Test
    void testCreateStageNegativeProjectByCancelled() {
        Mockito.when(projectRepository.getProjectById(anyLong())).thenReturn(stage.getProject());
        assertThrows(DataValidationException.class, () -> stageService.createStage(stageMapper.toDto(stage)));
        Mockito.verify(stageRepository, Mockito.times(0)).save(stage);
    }

    @Test
    void testCreateStageNegativeProjectByCompleted() {
        stage = Stage.builder()
                .stageId(1L)
                .stageName("stage")
                .project(Project.builder().id(1L).status(ProjectStatus.COMPLETED).build())
                .build();
        Mockito.when(projectRepository.getProjectById(anyLong())).thenReturn(stage.getProject());
        assertThrows(DataValidationException.class, () -> stageService.createStage(stageMapper.toDto(stage)));
        Mockito.verify(stageRepository, Mockito.times(0)).save(stage);
    }

    @Test
    void testCreateStagePositive(){
        stage = Stage.builder()
                .stageId(1L)
                .stageName("stage")
                .project(Project.builder().id(1L).name("project").status(ProjectStatus.CREATED).build())
                .stageRoles(List.of(StageRoles
                        .builder()
                        .id(1L)
                        .teamRole(TeamRole.ANALYST)
                        .count(1)
                        .build()))
                .build();

        Mockito.when(projectRepository.getProjectById(anyLong())).thenReturn(stage.getProject());
        Mockito.when(stageRepository.save(any())).thenReturn(stage);
        stageService.createStage(stageMapper.toDto(stage));
        Mockito.verify(stageMapper, Mockito.times(2)).toDto(stage);
    }
}
