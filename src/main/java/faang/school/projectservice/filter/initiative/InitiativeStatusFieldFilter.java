package faang.school.projectservice.filter.initiative;

import faang.school.projectservice.model.initiative.Initiative;

import java.util.stream.Stream;

public class InitiativeStatusFieldFilter implements InitiativeFieldFilter {

    @Override
    public boolean isApplicable(InitiativeFilterDto initiativeFilterDto) {
        return initiativeFilterDto.getStatus() != null;
    }

    @Override
    public Stream<Initiative> apply(Stream<Initiative> initiativeStream, InitiativeFilterDto initiativeFilterDto) {
        return initiativeStream.filter(initiative -> initiative.getStatus().equals(initiativeFilterDto.getStatus()));
    }
}
