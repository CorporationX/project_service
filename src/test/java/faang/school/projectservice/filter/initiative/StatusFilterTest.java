package faang.school.projectservice.filter.initiative;

import faang.school.projectservice.dto.initiative.InitiativeFilterDto;
import faang.school.projectservice.model.initiative.Initiative;
import faang.school.projectservice.model.initiative.InitiativeStatus;
import org.jetbrains.annotations.NotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class StatusFilterTest {
    private InitiativeFilterDto initiativeFilterDto;
    private StatusFilter statusFilter;
    private List<Initiative> initiatives;

    @BeforeEach
    void setup() {
        InitiativeFilterHelper filterHelper = new InitiativeFilterHelper();
        initiativeFilterDto = filterHelper.initiativeFilterDto();
        statusFilter = new StatusFilter();
        initiatives = filterHelper.initiatives();
    }

    @Test
    void testIsApplicableWhenStatusPatternIsNotNull() {
        assertTrue(statusFilter.isApplicable(initiativeFilterDto));
    }

    @Test
    void testIsApplicableWhenStatusPatternIsNull() {
        initiativeFilterDto.setStatusPattern(null);
        assertFalse(statusFilter.isApplicable(initiativeFilterDto));
    }

    @Test
    void testApplyWhenInitiativesMatchStatusPattern() {
        List<Initiative> filteredInitiatives = getInitiatives();
        assertEquals(2, initiatives.size());
        assertTrue(filteredInitiatives.containsAll(initiatives));
    }

    @Test
    void testApplyWhenNoInitiativesMatchStatusPattern() {
        initiativeFilterDto.setStatusPattern(InitiativeStatus.CLOSED);
        List<Initiative> filteredInitiatives = getInitiatives();
        assertEquals(0, filteredInitiatives.size());
        assertTrue(filteredInitiatives.isEmpty());
    }
    private @NotNull List<Initiative> getInitiatives() {
        return statusFilter.apply(initiatives
                        .stream(), initiativeFilterDto)
                .toList();
    }
}