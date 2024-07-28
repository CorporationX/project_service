package faang.school.projectservice.filter.stagefilter;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

class TeamRoleFilterTest {
    private TeamRoleFilter teamRoleFilter;
    private StageFilterDto stageFilterDto;
    List<Stage> stages;

    @BeforeEach
    void setup(){
        Stage stageFirst = new Stage();
        Stage stageSecond = new Stage();
        StageRoles stageRoles = new StageRoles();
        stageRoles.setTeamRole(TeamRole.ANALYST);
        stageFirst.setStageRoles(List.of(stageRoles));
        stageSecond.setStageRoles(List.of(stageRoles));
        teamRoleFilter = new TeamRoleFilter();
        stageFilterDto = new StageFilterDto();
        stageFilterDto = mock(StageFilterDto.class);
        stages = List.of(stageFirst, stageSecond);
    }

    @Test
    void testIsApplicableWhenTeamRolePatternIsNotNull(){
        when(stageFilterDto.getTeamRolePattern()).thenReturn(TeamRole.ANALYST);
        assertTrue(teamRoleFilter.isApplicable(stageFilterDto));
    }

    @Test
    void testIsApplicableWhenTeamRolePatternIsNull(){
        when(stageFilterDto.getTaskStatusPattern()).thenReturn(null);
        assertFalse(teamRoleFilter.isApplicable(stageFilterDto));
    }

    @Test
    void testApplyWhenStagesMatchTeamRolePattern(){
        List<Stage> filteredStages = stages;
        when(stageFilterDto.getTeamRolePattern()).thenReturn(TeamRole.ANALYST);
        filteredStages = teamRoleFilter.apply(filteredStages.stream(),stageFilterDto).toList();
        assertEquals(2, filteredStages.size());
        assertTrue(filteredStages.contains(filteredStages.get(0)));
        assertTrue(filteredStages.contains(filteredStages.get(1)));
    }

    @Test
    void testApplyWhenNoStagesMatchTeamRolePattern(){
        List<Stage> filteredStages = stages;
        when(stageFilterDto.getTeamRolePattern()).thenReturn(TeamRole.DESIGNER);
        filteredStages = teamRoleFilter.apply(filteredStages.stream(),stageFilterDto).toList();
        assertEquals(0, filteredStages.size());
        assertTrue(filteredStages.isEmpty());
    }
}