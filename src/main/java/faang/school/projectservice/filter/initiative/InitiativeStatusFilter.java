package faang.school.projectservice.filter.initiative;

import faang.school.projectservice.dto.filter.initiative.InitiativeFilterDto;
import faang.school.projectservice.model.initiative.Initiative;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class InitiativeStatusFilter implements InitiativeFilter {
    @Override
    public boolean isApplicable(InitiativeFilterDto filters) {
        return filters.status() != null;
    }

    @Override
    public Stream<Initiative> apply(Stream<Initiative> initiatives, InitiativeFilterDto filters) {
        return initiatives.filter(initiative -> initiative.getStatus().equals(filters.status()));
    }
}
