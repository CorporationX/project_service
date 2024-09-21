package faang.school.projectservice.service;


import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.service.stage.StageService;
import faang.school.projectservice.service.stage.executor.DeleteStageProcessor;
import faang.school.projectservice.service.stage.executor.DeleteStageStrategyExecutor;
import faang.school.projectservice.model.stage.strategy.DeleteStageTaskStrategy;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.stage_invitation.StageInvitationService;
import faang.school.projectservice.service.stage_roles.StageRolesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StageServiceTest {
    private Stage stage;
    private Project project;
    private StageDto stageDto;
    private long projectId = 100L;
    private long stageId = 1L;
    private List<Long> taskIds = List.of(1L, 2L, 3L);
    private List<Long> executorIds = List.of(1L, 2L, 3L);

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private StageService mockStageService;

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private StageRolesService stageRolesService;
    @Mock
    private StageInvitationService stageInvitationService;
    @Mock
    private DeleteStageProcessor deleteStageProcessor;
    @Mock
    private StageRepository stageRepository;

    @InjectMocks
    private StageService stageService;

    @Spy
    private StageMapper stageMapper;


    @Captor
    private ArgumentCaptor<Stage> stageCaptor;
    @Captor
    private ArgumentCaptor<DeleteStageStrategyExecutor> deleteStageStrategyExecutorCaptor;

    @BeforeEach
    public void setup() {
        TeamRole teamRole = TeamRole.DEVELOPER;

        StageRolesDto stageRolesDto = new StageRolesDto(teamRole, 5);
        Set<StageRolesDto> stageRolesDtos = Set.of(stageRolesDto);

        stageDto = new StageDto(
                stageId,
                "Development Stage",
                projectId,
                stageRolesDtos,
                taskIds,
                executorIds
        );
        project = new Project();
        project.setId(projectId);
        stage = new Stage();
        stage.setStageId(stageId);
        stage.setProject(project);

    }

    @Test
    void testSuccess_createStage() {
        StageRoles stageRoles = new StageRoles();

        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        when(stageMapper.toStage(stageDto)).thenReturn(stage);
        when(stageRepository.save(stage)).thenReturn(stage);
        when(stageRolesService.createStageRolesForStageById(stageId, stageDto.stageRolesDtos())).thenReturn(List.of(stageRoles));
        when(stageMapper.toStageDto(stage)).thenReturn(stageDto);

        StageDto result = stageService.createStage(stageDto);

        verify(projectRepository).getProjectById(projectId);
        verify(stageMapper).toStage(stageDto);
        verify(stageRepository).save(stage);
        verify(stageRolesService).createStageRolesForStageById(stageId, stageDto.stageRolesDtos());
        verify(stageMapper).toStageDto(stage);

        assertEquals(stageDto, result);
    }

    @Test
    void testUnsuccessful_createStage_shouldThrowException_whenProjectIsNotProvided() {
        when(projectRepository.getProjectById(projectId)).thenReturn(null);
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> stageService.createStage(stageDto),
                "Expected createStage() to throw, but it didn't"
        );

        assertEquals("Project not found by id: " + projectId, thrown.getMessage());
    }

    @Test
    void createStage_shouldThrowException_whenProjectDoesNotExist() {
        when(projectRepository.getProjectById(projectId)).thenReturn(null);

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> stageService.createStage(stageDto),
                "Expected createStage() to throw, but it didn't"
        );

        assertEquals("Project not found by id: " + projectId, thrown.getMessage());
    }

    @Test
    void testSuccess_deleteStage() {
        when(stageRepository.getById(stageId)).thenReturn(stage);

        stageService.deleteStage(stageId, DeleteStageTaskStrategy.CLOSE, 1L);

        verify(deleteStageProcessor, times(1)).process(stageCaptor.capture(), any(DeleteStageTaskStrategy.class), any(Long.class));
    }

    @Test
    void testUnsuccessful_deleteStage_shouldThrowException_whenStageDoesNotExist() {
        when(stageRepository.getById(stageId)).thenReturn(null);

        DataValidationException thrown = assertThrows(
                DataValidationException.class,
                () -> stageService.deleteStage(stageId, DeleteStageTaskStrategy.CLOSE, 1L),
                "Expected deleteStage() to throw, but it didn't"
        );
        assertEquals("Stage not found by id: " + stageId, thrown.getMessage());
    }


    @Test
    void testSuccess_getAllStagesByProjectId() {
        List<Stage> stages = List.of(new Stage());

        when(stageRepository.findAllStagesByProjectId(projectId)).thenReturn(stages);
        when(stageMapper.toStageDtos(stages)).thenReturn(List.of(stageDto));

        List<StageDto> result = stageService.getAllStagesByProjectId(projectId);

        assertEquals(List.of(stageDto), result);
    }




    @Test
    void testSuccess_updateStage() {
        when(stageRepository.getById(stageId)).thenReturn(stage);
        when(taskRepository.findAllById(taskIds)).thenReturn(List.of(new Task(), new Task(), new Task()));
        when(teamMemberRepository.findAllById(executorIds)).thenReturn(List.of(new TeamMember(), new TeamMember(), new TeamMember()));
        when(stageRepository.save(stage)).thenReturn(stage);
        when(stageMapper.toStageDto(stage)).thenReturn(stageDto);


        StageDto result = stageService.updateStage(stageDto);

        verify(stageRepository).getById(stageId);
        verify(taskRepository).findAllById(taskIds);
        verify(teamMemberRepository).findAllById(executorIds);
        verify(stageRepository).save(stage);
        verify(stageMapper).toStageDto(stage);



    }
}
