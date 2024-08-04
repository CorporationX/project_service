package faang.school.projectservice.filter.initiative;

import faang.school.projectservice.dto.initiative.InitiativeFilterDto;
import faang.school.projectservice.model.initiative.Initiative;

import java.util.stream.Stream;

public interface InitiativeFilter {

    boolean isApplicable(InitiativeFilterDto initiativeFilterDto);

    Stream<Initiative> apply(Stream<Initiative> initiativeStream,
                             InitiativeFilterDto initiativeFilterDto);
}
