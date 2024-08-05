package faang.school.projectservice.filter.initiative;

import faang.school.projectservice.dto.filter.initiative.InitiativeFilterDto;
import faang.school.projectservice.model.initiative.Initiative;

import java.util.stream.Stream;

public interface InitiativeFilter {
    boolean isApplicable(InitiativeFilterDto filters);

    Stream<Initiative> apply(Stream<Initiative> initiatives, InitiativeFilterDto filters);
}
