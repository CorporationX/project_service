package faang.school.projectservice.service.stage;

import faang.school.projectservice.model.dto.stage.StageDto;
import faang.school.projectservice.model.dto.stage.StageFilterDto;
import faang.school.projectservice.model.dto.stage.StageRolesDto;
import faang.school.projectservice.model.filter.stage.StageFilter;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.model.mapper.stage.StageMapper;
import faang.school.projectservice.model.entity.*;
import faang.school.projectservice.model.entity.stage.Stage;
import faang.school.projectservice.model.entity.stage.StageRoles;
import faang.school.projectservice.model.entity.stage_invitation.StageInvitation;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.impl.stage.StageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StageServiceImplTest {

    @Mock
    private StageRepository stageRepository;

    @Mock
    private ProjectService projectService;

    @Mock
    private List<StageFilter> stageFilters;

    @Spy
    private StageMapper stageMapper;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private StageInvitationRepository stageInvitationRepository;

    @Captor
    ArgumentCaptor<List<Stage>> captor;

    @InjectMocks
    private StageServiceImpl stageService;
    private Stage stage;

    @BeforeEach
    void setup() {
        stage = Stage
                .builder()
                .stageRoles(List.of(StageRoles.builder().teamRole(TeamRole.MANAGER).build()))
                .tasks(List.of(Task.builder().status(TaskStatus.REVIEW).build()))
                .executors(List.of())
                .project(Project
                        .builder()
                        .teams(List.of(Team
                                .builder()
                                .teamMembers(List.of(TeamMember.builder().roles(List.of(TeamRole.ANALYST)).build()))
                                .build()))
                        .build())
                .stageName("Test stage")
                .build();
    }

    @Test
    void testCreateStage() {
        StageDto stageDto = StageDto
                .builder()
                .projectId(1L)
                .build();

        stageService.createStage(stageDto);

        verify(projectService).getProject(anyLong());
        verify(stageRepository).save(any());
    }

    @Test
    void testGetProjectStagesOk() {
        StageFilterDto filters = StageFilterDto
                .builder()
                .role(TeamRole.MANAGER)
                .taskStatus(TaskStatus.REVIEW)
                .build();

        Stage stage2 = Stage
                .builder()
                .stageRoles(List.of(StageRoles.builder().teamRole(TeamRole.ANALYST).build()))
                .tasks(List.of())
                .build();

        Project project = Project.builder().stages(List.of(stage, stage2)).build();

        when(projectService.getProject(1L)).thenReturn(project);

        stageService.getProjectStages(1L, filters);

        verify(projectService).getProject(1L);
        verify(stageMapper).toStageDtos(captor.capture());

        assertEquals(2, captor.getValue().size());
    }

    @Test
    void testDeleteStageOk() {
        when(stageRepository.getById(anyLong())).thenReturn(stage);

        stageService.deleteStage(1L);

        verify(taskRepository).deleteAll(stage.getTasks());
        verify(stageRepository).delete(stage);
    }

    @Test
    void testUpdateStage() {
        StageRolesDto rolesDto = StageRolesDto
                .builder()
                .teamRole(TeamRole.ANALYST)
                .count(1)
                .build();

        when(stageRepository.getById(anyLong())).thenReturn(stage);

        stageService.updateStage(1L, rolesDto);

        verify(stageInvitationRepository).save(any(StageInvitation.class));
        verify(stageMapper).toDto(stage);
    }

    @Test
    void testGetSpecificStage() {
        when(stageRepository.getById(anyLong())).thenReturn(stage);
        stageService.getSpecificStage(1L);

        verify(stageRepository).getById(anyLong());
        verify(stageMapper).toDto(stage);
    }

    @Test
    void testGetStages() {
        Project project = Project.builder().stages(List.of(stage)).build();

        when(projectService.getProject(anyLong())).thenReturn(project);

        stageService.getStages(1L);

        verify(projectService).getProject(anyLong());
        verify(stageMapper).toStageDtos(project.getStages());
    }
}