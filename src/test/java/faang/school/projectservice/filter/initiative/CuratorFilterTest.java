package faang.school.projectservice.filter.initiative;

import faang.school.projectservice.dto.initiative.InitiativeFilterDto;
import faang.school.projectservice.model.initiative.Initiative;
import org.jetbrains.annotations.NotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class CuratorFilterTest {
    private InitiativeFilterDto initiativeFilterDto;
    private CuratorFilter curatorFilter;
    private List<Initiative> initiatives;

    @BeforeEach
    void setup() {
        InitiativeFilterHelper filterHelper = new InitiativeFilterHelper();
        initiativeFilterDto = filterHelper.initiativeFilterDto();
        curatorFilter = new CuratorFilter();
        initiatives = filterHelper.initiatives();
    }

    @Test
    void testIsApplicableWhenCuratorPatternIsNotNull() {
        assertTrue(curatorFilter.isApplicable(initiativeFilterDto));
    }

    @Test
    void testIsApplicableWhenCuratorPatternIsNull() {
        initiativeFilterDto.setCuratorPattern(null);
        assertFalse(curatorFilter.isApplicable(initiativeFilterDto));
    }

    @Test
    void testApplyWhenInitiativesMatchCuratorPattern() {
        List<Initiative> filteredInitiatives = getInitiatives();
        assertEquals(2, initiatives.size());
        assertTrue(filteredInitiatives.containsAll(initiatives));
    }

    @Test
    void testApplyWhenNoInitiativesMatchStatusPattern() {
        Long curatorIdNegative = 2L;
        initiativeFilterDto.setCuratorPattern(curatorIdNegative);
        List<Initiative> filteredInitiatives = getInitiatives();
        assertEquals(0, filteredInitiatives.size());
        assertTrue(filteredInitiatives.isEmpty());
    }

    private @NotNull List<Initiative> getInitiatives() {
        return curatorFilter.apply(initiatives
                        .stream(), initiativeFilterDto)
                .toList();
    }
}