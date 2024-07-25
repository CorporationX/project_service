package faang.school.projectservice.filter.initiative;

import faang.school.projectservice.model.initiative.Initiative;

import java.util.function.Predicate;

public interface InitiativeFieldFilter {

    boolean isApplicable(InitiativeFilterDto initiativeFilterDto);

    Predicate<Initiative> apply(InitiativeFilterDto initiativeFilterDto);
}
