package faang.school.projectservice.filter.stage;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class StageRoleFilterTest {

    private StageRoleFilter stageRoleFilter;
    private StageFilterDto stageFilterDto;

    @BeforeEach
    void setUp() {
        stageRoleFilter = new StageRoleFilter();
        stageFilterDto = Mockito.mock(StageFilterDto.class);
    }

    @Test
    void isApplicableTest_ShouldReturnTrueWhenTeamRoleIsNotNull() {
        TeamRole teamRole = TeamRole.MANAGER;
        when(stageFilterDto.getTeamRole()).thenReturn(teamRole);

        boolean result = stageRoleFilter.isApplicable(stageFilterDto);

        assertTrue(result);
    }

    @Test
    void isApplicableTest_ShouldReturnFalseWhenTeamRoleIsNull() {
        when(stageFilterDto.getTeamRole()).thenReturn(null);

        boolean result = stageRoleFilter.isApplicable(stageFilterDto);

        assertFalse(result);
    }

    @Test
    void applyTest_ShouldReturnStageWhenTeamRoleMatches() {
        TeamRole teamRole = TeamRole.MANAGER;
        Stage stage1 = createStageWithRole(teamRole);
        Stage stage2 = createStageWithRole(TeamRole.DEVELOPER);
        List<Stage> stages = Arrays.asList(stage1, stage2);

        when(stageFilterDto.getTeamRole()).thenReturn(teamRole);

        Stream<Stage> filteredStages = stageRoleFilter.apply(stages.stream(), stageFilterDto);
        List<Stage> result = filteredStages.toList();

        assertEquals(1, result.size());
        assertEquals(stage1, result.get(0));
    }

    @Test
    void applyTest_ShouldNotReturnStageWhenNoStageHasMatchingRole() {
        TeamRole teamRole = TeamRole.MANAGER;
        Stage stage1 = createStageWithRole(TeamRole.DEVELOPER);
        Stage stage2 = createStageWithRole(TeamRole.TESTER);
        List<Stage> stages = Arrays.asList(stage1, stage2);

        when(stageFilterDto.getTeamRole()).thenReturn(teamRole);

        Stream<Stage> filteredStages = stageRoleFilter.apply(stages.stream(), stageFilterDto);
        List<Stage> result = filteredStages.toList();

        assertTrue(result.isEmpty());
    }

    private Stage createStageWithRole(TeamRole teamRole) {
        Stage stage = Mockito.mock(Stage.class);
        StageRoles stageRoles = Mockito.mock(StageRoles.class);
        when(stageRoles.getTeamRole()).thenReturn(teamRole);
        when(stage.getStageRoles()).thenReturn(Collections.singletonList(stageRoles));
        return stage;
    }
}