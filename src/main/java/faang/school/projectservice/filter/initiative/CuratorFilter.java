package faang.school.projectservice.filter.initiative;

import faang.school.projectservice.dto.client.InitiativeFilterDto;
import faang.school.projectservice.model.initiative.Initiative;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class CuratorFilter implements InitiativeFilter {
    @Override
    public boolean isApplicable(InitiativeFilterDto initiativeFilterDto) {
        return initiativeFilterDto.getCuratorPattern() != null;
    }

    @Override
    public Stream<Initiative> apply(Stream<Initiative> initiativeStream,
                                    InitiativeFilterDto initiativeFilterDto) {
        return initiativeStream
                .filter(filter -> filter
                        .getCurator()
                        .getId()
                        .equals(initiativeFilterDto.getCuratorPattern()));
    }
}
