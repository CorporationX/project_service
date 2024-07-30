package faang.school.projectservice.filter.initiative;

import faang.school.projectservice.dto.initiative.InitiativeFilterDto;
import faang.school.projectservice.model.initiative.Initiative;
import faang.school.projectservice.model.initiative.InitiativeStatus;
import org.jetbrains.annotations.NotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

public class MixInitiativeFilterTest {
    private InitiativeFilterDto initiativeFilterDto;
    private List<Initiative> initiatives;
    List<InitiativeFilter> filters;

    @BeforeEach
    void setup() {
        InitiativeFilterHelper filterHelper = new InitiativeFilterHelper();
        initiatives = filterHelper.initiatives();
        StatusFilter statusFilter = new StatusFilter();
        CuratorFilter curatorFilter = new CuratorFilter();
        filters = List.of(curatorFilter, statusFilter);
        initiativeFilterDto = filterHelper.initiativeFilterDto();
    }

    @Test
    void testApplyWhenInitiativesMatchMixFilers() {
        List<Initiative> filteredInitiatives = getFilteredInitiatives();
        assertEquals(2, filteredInitiatives.size());
        assertTrue(filteredInitiatives.containsAll(initiatives));
    }

    @Test
    void testApplyWhenNoInitiativesMatchMixFilers() {
        Long curatorIdNegative = 2L;
        initiativeFilterDto.setCuratorPattern(curatorIdNegative);
        initiativeFilterDto.setStatusPattern(InitiativeStatus.CLOSED);
        List<Initiative> filteredInitiatives = getFilteredInitiatives();
        assertEquals(0, filteredInitiatives.size());
        assertTrue(filteredInitiatives.isEmpty());
    }

    private @NotNull List<Initiative> getFilteredInitiatives() {
        return filters.stream()
                .reduce(initiatives.stream(),
                        (stream, filter) -> filter.apply(stream, initiativeFilterDto),
                        Stream::concat)
                .toList();
    }
}
