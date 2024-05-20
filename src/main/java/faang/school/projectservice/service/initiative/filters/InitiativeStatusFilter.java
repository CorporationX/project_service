package faang.school.projectservice.service.initiative.filters;

import faang.school.projectservice.dto.initiative.InitiativeFilterDto;
import faang.school.projectservice.model.initiative.Initiative;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class InitiativeStatusFilter implements InitiativeFilter {
    @Override
    public boolean isAcceptable(InitiativeFilterDto filters) {
        return filters.getStatus() != null;
    }

    @Override
    public Stream<Initiative> apply(Stream<Initiative> initiatives, InitiativeFilterDto filters) {
        return initiatives.filter(initiative -> initiative.getStatus() == filters.getStatus());
    }
}
