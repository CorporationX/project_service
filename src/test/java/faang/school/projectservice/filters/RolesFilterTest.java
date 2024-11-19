package faang.school.projectservice.filters;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class RolesFilterTest {
    private final StageFilter rolesFilter = new RolesFilter();
    private Stream<Stage> stages;

    @BeforeEach
    void setUp() {
        stages = initStages();
    }

    @Test
    @DisplayName("Is filter applicable with filter in dto")
    void rolesFilterTest_isFilterApplicableWithFilterInDto() {
        StageFilterDto stageFilterDto = initStageFilterDto(TeamRole.TESTER);
        boolean result = rolesFilter.isApplicable(stageFilterDto);
        assertTrue(result);
    }

    @Test
    @DisplayName("Is filter applicable without filter in dto")
    void rolesFilterTest_isFilterApplicableWithoutFilterInDto() {
        StageFilterDto stageFilterDto = initStageFilterDto(null);
        boolean result = rolesFilter.isApplicable(stageFilterDto);
        assertFalse(result);
    }

    @Test
    @DisplayName("Filter stages")
    void rolesFilterTest_filterStages() {
        StageFilterDto stageFilterDto = initStageFilterDto(TeamRole.TESTER);
        Stage.builder()
                .stageId(2L)
                .stageName("Stage 2")
                .stageRoles(List.of(
                        StageRoles.builder()
                                .id(2L)
                                .teamRole(TeamRole.TESTER)
                                .count(1)
                                .build()))
                .build();
        var result = rolesFilter.apply(stages, stageFilterDto).toList();
        assertEquals(1L, result.size());
    }

    @Test
    @DisplayName("Filter stages from which none match filter")
    void rolesFilterTest_filterStagesFromWhichNoneMatchFilter() {
        StageFilterDto stageFilterDto = initStageFilterDto(TeamRole.OWNER);
        var result = rolesFilter.apply(stages, stageFilterDto).toList();
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Filter empty stages")
    void rolesFilterTest_filterEmptyStages() {
        StageFilterDto stageFilterDto = initStageFilterDto(TeamRole.OWNER);
        var result = rolesFilter.apply(Stream.empty(), stageFilterDto).toList();
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Filter stages with null arguments")
    void rolesFilterTest_filterStagesWithNullArguments() {
        StageFilterDto stageFilterDto = initStageFilterDto(TeamRole.OWNER);
        assertThrows(NullPointerException.class, () -> rolesFilter.apply(null, stageFilterDto));
        assertThrows(NullPointerException.class, () -> rolesFilter.apply(null, null));
    }

    private Stream<Stage> initStages() {
        return Stream.of(
                initStage(1L, "Stage 1", List.of(initStageRole(1L, TeamRole.DEVELOPER, 1))),
                initStage(2L, "Stage 2", List.of(initStageRole(2L, TeamRole.TESTER, 1))),
                initStage(3L, "Stage 3", List.of(initStageRole(3L, TeamRole.DESIGNER, 1))));
    }

    private StageFilterDto initStageFilterDto(TeamRole teamRole) {
        return StageFilterDto.builder()
                .roleFilter(teamRole)
                .build();
    }

    private StageRoles initStageRole(Long id, TeamRole role, int count) {
        return StageRoles.builder()
                .id(id)
                .teamRole(role)
                .count(count)
                .build();
    }

    private Stage initStage(Long id, String name, List<StageRoles> stageRoles) {
        return Stage.builder()
                .stageId(id)
                .stageName(name)
                .stageRoles(stageRoles)
                .build();
    }
}