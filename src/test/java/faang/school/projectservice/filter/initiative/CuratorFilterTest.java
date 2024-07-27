package faang.school.projectservice.filter.initiative;

import faang.school.projectservice.dto.client.InitiativeFilterDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.initiative.Initiative;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

class CuratorFilterTest {
    private Initiative initiativeFirst;
    private Initiative initiativeSecond;
    private CuratorFilter curatorFilter;
    private InitiativeFilterDto initiativeFilterDto;
    private Long curatorId;
    private Long curatorIdNegative;
    private TeamMember teamMember;

    @BeforeEach
    void setup() {
        curatorId = 1L;
        curatorIdNegative = 2L;
        teamMember = TeamMember.builder()
                .id(curatorId)
                .build();
        initiativeFirst = new Initiative();
        initiativeSecond = new Initiative();
        curatorFilter = new CuratorFilter();
        initiativeFilterDto = mock(InitiativeFilterDto.class);
    }

    @Test
    void testIsApplicableWhenCuratorPatternIsNotNull() {
        when(initiativeFilterDto.getCuratorPattern()).thenReturn(curatorId);
        assertTrue(curatorFilter.isApplicable(initiativeFilterDto));
    }

    @Test
    void testIsApplicableWhenCuratorPatternIsNull() {
        when(initiativeFilterDto.getCuratorPattern()).thenReturn(null);
        assertFalse(curatorFilter.isApplicable(initiativeFilterDto));
    }

    @Test
    void testApplyWhenInitiativesMatchCuratorPattern() {
        List<Initiative> filteredInitiatives = getInitiatives();
        when(initiativeFilterDto.getCuratorPattern()).thenReturn(curatorId);

        filteredInitiatives = curatorFilter.apply(filteredInitiatives
                        .stream(), initiativeFilterDto)
                .toList();
        assertEquals(2, filteredInitiatives.size());
        assertTrue(filteredInitiatives.contains(filteredInitiatives.get(0)));
        assertTrue(filteredInitiatives.contains(filteredInitiatives.get(1)));
    }

    @Test
    void testApplyWhenNoInitiativesMatchCuratorPattern() {
        List<Initiative> filteredInitiatives = getInitiatives();
        when(initiativeFilterDto.getCuratorPattern()).thenReturn(curatorIdNegative);
        filteredInitiatives = curatorFilter.apply(filteredInitiatives
                        .stream(), initiativeFilterDto)
                .toList();
        assertEquals(0, filteredInitiatives.size());
        assertTrue(filteredInitiatives.isEmpty());
    }

    List<Initiative> getInitiatives() {
        initiativeFirst.setCurator(teamMember);
        initiativeSecond.setCurator(teamMember);
        return List.of(initiativeFirst, initiativeSecond);
    }

}