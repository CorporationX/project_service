package faang.school.projectservice.filter.initiative;

import faang.school.projectservice.dto.filter.initiative.InitiativeFilterDto;
import faang.school.projectservice.model.initiative.Initiative;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class InitiativeCuratorFilter implements InitiativeFilter {
    @Override
    public boolean isApplicable(InitiativeFilterDto filters) {
        return filters.curatorId() != null;
    }

    @Override
    public Stream<Initiative> apply(Stream<Initiative> initiatives, InitiativeFilterDto filters) {
        return initiatives.filter(initiative -> initiative.getCurator() != null &&
                initiative.getCurator().getId().equals(filters.curatorId()));
    }
}
