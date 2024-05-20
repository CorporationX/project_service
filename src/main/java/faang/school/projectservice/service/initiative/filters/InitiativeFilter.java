package faang.school.projectservice.service.initiative.filters;

import faang.school.projectservice.dto.initiative.InitiativeFilterDto;
import faang.school.projectservice.model.initiative.Initiative;

import java.util.stream.Stream;

public interface InitiativeFilter {
    boolean isAcceptable(InitiativeFilterDto filters);

    Stream<Initiative> apply(Stream<Initiative> initiatives, InitiativeFilterDto filters);
}
