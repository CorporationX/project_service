package faang.school.projectservice.filter.initiative;

import faang.school.projectservice.model.initiative.Initiative;

import java.util.stream.Stream;


public interface InitiativeFieldFilter {

    boolean isApplicable(InitiativeFilterDto initiativeFilterDto);

    Stream<Initiative> apply(Stream<Initiative> entity, InitiativeFilterDto initiativeFilterDto);
}
