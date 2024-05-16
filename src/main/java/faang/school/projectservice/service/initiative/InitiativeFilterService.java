package faang.school.projectservice.service.initiative;

import faang.school.projectservice.dto.initiative.InitiativeFilterDto;
import faang.school.projectservice.model.initiative.Initiative;

import java.util.stream.Stream;

public interface InitiativeFilterService {
    Stream<Initiative> applyAll(Stream<Initiative> initiatives, InitiativeFilterDto filterDto);
}
