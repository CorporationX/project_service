package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.StageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StageRolesServiceTest {
    @InjectMocks
    private StageRolesService stageRolesService;

    @Mock
    private StageService stageService;

    @Mock
    private StageRepository stageRepository;

    private Long id;
    private Stage stage;

    @BeforeEach
    public void setUp() {
        id = 1L;

        StageRoles stageRoles = StageRoles
                .builder()
                .count(2)
                .teamRole(TeamRole.OWNER)
                .build();

        TeamMember teamMember = TeamMember
                .builder()
                .roles(List.of(TeamRole.OWNER))
                .build();

        List<TeamMember> executors = new ArrayList<>(Collections.singletonList(teamMember));
        List<StageRoles> stageRolesList = new ArrayList<>(Collections.singletonList(stageRoles));

        stage = Stage
                .builder()
                .stageId(id)
                .executors(executors)
                .stageRoles(stageRolesList)
                .build();

        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    @DisplayName("testing that addStageRoles() calls all his beans correctly")
    public void testAddStageRoles() {
        when(stageRepository.getById(stage.getStageId())).thenReturn(stage);
        doNothing().when(stageService).updateStage(stage.getStageId());
        when(stageRepository.save(stage)).thenReturn(stage);

        stageRolesService.addStageRoles(StageRolesDto
                .builder()
                        .teamRole(TeamRole.OWNER)
                        .count(2)
                        .stageId(id)
                .build());

        verify(stageRepository).getById(stage.getStageId());
        verify(stageService).updateStage(stage.getStageId());
        verify(stageRepository).save(stage);
    }

    @Test
    @DisplayName("testing that setStageRoles() calls all his beans correctly")
    public void testSetStageRoles() {
        when(stageRepository.getById(stage.getStageId())).thenReturn(stage);
        doNothing().when(stageService).updateStage(stage.getStageId());
        when(stageRepository.save(stage)).thenReturn(stage);

        stageRolesService.addStageRoles(StageRolesDto
                .builder()
                .teamRole(TeamRole.OWNER)
                .count(2)
                .stageId(id)
                .build());

        verify(stageRepository).getById(stage.getStageId());
        verify(stageService).updateStage(stage.getStageId());
        verify(stageRepository).save(stage);
    }

    @Test
    @DisplayName("testing that getAllStageRoles() calls all his beans correctly")
    public void testGetAllStageRoles() {
        when(stageRepository.getById(stage.getStageId())).thenReturn(stage);

        stageRolesService.getAllStageRoles(stage.getStageId());

        verify(stageRepository).getById(stage.getStageId());
    }
}
