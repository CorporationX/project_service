package faang.school.projectservice.service.initiative;

import faang.school.projectservice.dto.initiative.InitiativeFilterDto;
import faang.school.projectservice.model.initiative.Initiative;
import faang.school.projectservice.service.initiative.filters.InitiativeFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class InitiativeFilterServiceImpl implements InitiativeFilterService {
    private final List<InitiativeFilter> filters;

    public Stream<Initiative> applyAll(Stream<Initiative> initiatives, InitiativeFilterDto filterDto) {
        return filters.stream()
                .filter(filter -> filter.isAcceptable(filterDto))
                .flatMap(filter -> filter.apply(initiatives, filterDto));
    }
}
