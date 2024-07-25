package faang.school.projectservice.filter.initiative;

import faang.school.projectservice.model.initiative.Initiative;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
public class InitiativeStatusFieldFilter implements InitiativeFieldFilter {

    @Override
    public boolean isApplicable(InitiativeFilterDto initiativeFilterDto) {
        return initiativeFilterDto.getStatus() != null;
    }

    @Override
    public Predicate<Initiative> apply(InitiativeFilterDto initiativeFilterDto) {
        return initiative -> initiative.getStatus().equals(initiativeFilterDto.getStatus());
    }
}
