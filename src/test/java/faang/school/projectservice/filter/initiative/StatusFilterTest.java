package faang.school.projectservice.filter.initiative;

import faang.school.projectservice.dto.client.InitiativeFilterDto;
import faang.school.projectservice.model.initiative.Initiative;
import faang.school.projectservice.model.initiative.InitiativeStatus;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

class StatusFilterTest {
    private Initiative initiativeFirst;
    private Initiative initiativeSecond;
    private StatusFilter statusFilter;
    private InitiativeFilterDto initiativeFilterDto;

    @BeforeEach
    void setup() {
        initiativeFirst = new Initiative();
        initiativeSecond = new Initiative();
        statusFilter = new StatusFilter();
        initiativeFilterDto = mock(InitiativeFilterDto.class);
    }

    @Test
    void testIsApplicableWhenStatusPatternIsNotNull() {
        when(initiativeFilterDto.getStatusPattern()).thenReturn(InitiativeStatus.DONE);
        assertTrue(statusFilter.isApplicable(initiativeFilterDto));
    }

    @Test
    void testIsApplicableWhenStatusPatternIsNull() {
        when(initiativeFilterDto.getCuratorPattern()).thenReturn(null);
        assertFalse(statusFilter.isApplicable(initiativeFilterDto));
    }

    @Test
    void testApplyWhenInitiativesMatchStatusPattern() {
        List<Initiative> filteredInitiatives = getInitiatives();
        when(initiativeFilterDto.getStatusPattern()).thenReturn(InitiativeStatus.DONE);

        filteredInitiatives = statusFilter.apply(filteredInitiatives
                        .stream(), initiativeFilterDto)
                .toList();
        assertEquals(2, filteredInitiatives.size());
        assertTrue(filteredInitiatives.contains(filteredInitiatives.get(0)));
        assertTrue(filteredInitiatives.contains(filteredInitiatives.get(1)));
    }

    @Test
    void testApplyWhenNoInitiativesMatchStatusPattern() {
        List<Initiative> filteredInitiatives = getInitiatives();
        when(initiativeFilterDto.getStatusPattern()).thenReturn(InitiativeStatus.CLOSED);
        filteredInitiatives = statusFilter.apply(filteredInitiatives
                        .stream(), initiativeFilterDto)
                .toList();
        assertEquals(0, filteredInitiatives.size());
        assertTrue(filteredInitiatives.isEmpty());
    }

    List<Initiative> getInitiatives() {
        initiativeFirst.setStatus(InitiativeStatus.DONE);
        initiativeSecond.setStatus(InitiativeStatus.DONE);
        return List.of(initiativeFirst, initiativeSecond);
    }

}