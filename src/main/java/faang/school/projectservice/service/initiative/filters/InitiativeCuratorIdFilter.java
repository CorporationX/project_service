package faang.school.projectservice.service.initiative.filters;

import faang.school.projectservice.dto.initiative.InitiativeFilterDto;
import faang.school.projectservice.model.initiative.Initiative;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class InitiativeCuratorIdFilter implements InitiativeFilter {
    @Override
    public boolean isAcceptable(InitiativeFilterDto filters) {
        return filters.getCuratorId() != null;
    }

    @Override
    public Stream<Initiative> apply(Stream<Initiative> initiatives, InitiativeFilterDto filters) {
        return initiatives.filter(initiative -> initiative.getCurator().getUserId().equals(filters.getCuratorId()));
    }
}
