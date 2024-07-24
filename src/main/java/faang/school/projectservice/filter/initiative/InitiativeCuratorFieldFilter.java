package faang.school.projectservice.filter.initiative;

import faang.school.projectservice.model.initiative.Initiative;

import java.util.stream.Stream;

public class InitiativeCuratorFieldFilter implements InitiativeFieldFilter {

    @Override
    public boolean isApplicable(InitiativeFilterDto initiativeFilterDto) {
        return initiativeFilterDto.getCuratorId() != null;
    }

    @Override
    public Stream<Initiative> apply(Stream<Initiative> initiativeStream, InitiativeFilterDto initiativeFilterDto) {
        return initiativeStream.filter(initiative -> initiative.getCurator().getId().equals(initiativeFilterDto.getCuratorId()));
    }
}
