package faang.school.projectservice.filter.initiative;

import faang.school.projectservice.dto.initiative.InitiativeFilterDto;
import faang.school.projectservice.model.initiative.Initiative;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class StatusFilter implements InitiativeFilter {
    @Override
    public boolean isApplicable(InitiativeFilterDto initiativeFilterDto) {
        return initiativeFilterDto.getStatusPattern() != null;
    }

    @Override
    public Stream<Initiative> apply(Stream<Initiative> initiativeStream,
                                    InitiativeFilterDto initiativeFilterDto) {
        return initiativeStream
                .filter(filter -> filter
                        .getStatus()
                        .equals(initiativeFilterDto.getStatusPattern()));
    }
}
