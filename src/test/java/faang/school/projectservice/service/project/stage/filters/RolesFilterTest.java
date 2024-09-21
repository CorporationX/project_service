package faang.school.projectservice.service.project.stage.filters;

import faang.school.projectservice.dto.project.stage.StageFilterDto;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RolesFilterTest {
    private final StageFilter rolesFilter = new RolesFilter();
    Stream<Stage> stages;

    @BeforeEach
    void setUp() {
        stages = initStages();
    }

    @Test
    @DisplayName("Is filter applicable with filter in dto")
    void rolesFilterTest_isFilterApplicableWithFilterInDto() {
        StageFilterDto stageFilterDto = StageFilterDto.builder()
                .roleFilter(TeamRole.TESTER)
                .build();

        boolean result = rolesFilter.isApplicable(stageFilterDto);

        assertTrue(result);
    }

    @Test
    @DisplayName("Is filter applicable without filter in dto")
    void rolesFilterTest_isFilterApplicableWithoutFilterInDto() {
        StageFilterDto stageFilterDto = StageFilterDto.builder()
                .build();

        boolean result = rolesFilter.isApplicable(stageFilterDto);

        assertFalse(result);
    }

    @Test
    @DisplayName("Filter stages")
    void rolesFilterTest_filterStages() {
        StageFilterDto stageFilterDto = StageFilterDto.builder()
                .roleFilter(TeamRole.TESTER)
                .build();
        Stage stage = Stage.builder()
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
        assertTrue(result.contains(stage));
    }

    @Test
    @DisplayName("Filter stages from which none match filter")
    void rolesFilterTest_filterStagesFromWhichNoneMatchFilter() {
        StageFilterDto stageFilterDto = StageFilterDto.builder()
                .roleFilter(TeamRole.OWNER)
                .build();

        var result = rolesFilter.apply(stages, stageFilterDto).toList();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Filter empty stages")
    void rolesFilterTest_filterEmptyStages() {
        StageFilterDto stageFilterDto = StageFilterDto.builder()
                .roleFilter(TeamRole.OWNER)
                .build();

        var result = rolesFilter.apply(Stream.empty(), stageFilterDto).toList();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Filter stages with null arguments")
    void rolesFilterTest_filterStagesWithNullArguments() {
        StageFilterDto stageFilterDto = StageFilterDto.builder()
                .roleFilter(TeamRole.OWNER)
                .build();

        assertThrows(NullPointerException.class, () -> rolesFilter.apply(null, stageFilterDto));
        assertThrows(NullPointerException.class, () -> rolesFilter.apply(stages, null));
        assertThrows(NullPointerException.class, () -> rolesFilter.apply(null, null));
    }

    private Stream<Stage> initStages() {
        return Stream.of(
                Stage.builder()
                        .stageId(1L)
                        .stageName("Stage 1")
                        .stageRoles(List.of(
                                StageRoles.builder()
                                        .id(1L)
                                        .teamRole(TeamRole.DEVELOPER)
                                        .count(1)
                                        .build()))
                        .build(),
                Stage.builder()
                        .stageId(2L)
                        .stageName("Stage 2")
                        .stageRoles(List.of(
                                StageRoles.builder()
                                        .id(2L)
                                        .teamRole(TeamRole.TESTER)
                                        .count(1)
                                        .build()))
                        .build(),
                Stage.builder()
                        .stageId(3L)
                        .stageName("Stage 3")
                        .stageRoles(List.of(
                                StageRoles.builder()
                                        .id(3L)
                                        .teamRole(TeamRole.DESIGNER)
                                        .count(1)
                                        .build()))
                        .build()
        );
    }
}
