package faang.school.projectservice.filter.stagefilter;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import org.jetbrains.annotations.NotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class TeamRoleFilterTest {
    private TeamRoleFilter teamRoleFilter;
    private StageFilterDto stageFilterDto;
    private List<Stage> stages;

    @BeforeEach
    void setup(){
        StageFilterHelper filterHelper = new StageFilterHelper();
        stages = filterHelper.stages();
        teamRoleFilter = new TeamRoleFilter();
        stageFilterDto = filterHelper.stageFilterDto();
    }

    @Test
    void testIsApplicableWhenTeamRolePatternIsNotNull(){
        assertTrue(teamRoleFilter.isApplicable(stageFilterDto));
    }

    @Test
    void testIsApplicableWhenTeamRolePatternIsNull(){
        stageFilterDto.setTeamRolePattern(null);
        assertFalse(teamRoleFilter.isApplicable(stageFilterDto));
    }

    @Test
    void testApplyWhenStagesMatchTeamRolePattern(){
        List<Stage> filteredStages = getStages();
        assertEquals(2, filteredStages.size());
        assertTrue(filteredStages.containsAll(stages));
    }

    @Test
    void testApplyWhenNoStagesMatchTeamRolePattern(){
        stageFilterDto.setTeamRolePattern(TeamRole.DESIGNER);
        List<Stage> filteredStages = getStages();
        assertEquals(0, filteredStages.size());
        assertTrue(filteredStages.isEmpty());
    }

    private @NotNull List<Stage> getStages() {
        return teamRoleFilter.apply(stages
                        .stream(), stageFilterDto)
                .toList();
    }
}