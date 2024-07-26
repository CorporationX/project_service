package faang.school.projectservice.filter.initiative;

import faang.school.projectservice.dto.client.InitiativeFilterDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.initiative.Initiative;
import faang.school.projectservice.model.initiative.InitiativeStatus;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

public class MixFilterTest {
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
    void testApplyWhenInitiativesMatchMixFilers() {
        List<Initiative> filteredInitiatives = getInitiatives();
        when(initiativeFilterDto.getCuratorPattern()).thenReturn(curatorId);
        when(initiativeFilterDto.getStatusPattern()).thenReturn(InitiativeStatus.DONE);
        filteredInitiatives = curatorFilter.apply(filteredInitiatives
                        .stream(), initiativeFilterDto)
                .toList();
        assertEquals(2, filteredInitiatives.size());
        assertTrue(filteredInitiatives.contains(filteredInitiatives.get(0)));
        assertTrue(filteredInitiatives.contains(filteredInitiatives.get(1)));
    }

    @Test
    void testApplyWhenNoInitiativesMatchMixFilers() {
        List<Initiative> filteredInitiatives = getInitiatives();
        when(initiativeFilterDto.getCuratorPattern()).thenReturn(curatorIdNegative);
        when(initiativeFilterDto.getStatusPattern()).thenReturn(InitiativeStatus.CLOSED);
        filteredInitiatives = curatorFilter.apply(filteredInitiatives
                        .stream(), initiativeFilterDto)
                .toList();
        assertEquals(0, filteredInitiatives.size());
        assertTrue(filteredInitiatives.isEmpty());
    }

    List<Initiative> getInitiatives() {
        initiativeFirst.setCurator(teamMember);
        initiativeSecond.setCurator(teamMember);
        initiativeFirst.setStatus(InitiativeStatus.DONE);
        initiativeSecond.setStatus(InitiativeStatus.DONE);
        return List.of(initiativeFirst, initiativeSecond);
    }
}
