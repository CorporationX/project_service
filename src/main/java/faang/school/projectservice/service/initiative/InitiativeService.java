package faang.school.projectservice.service.initiative;

import faang.school.projectservice.dto.initiative.InitiativeDto;
import faang.school.projectservice.dto.initiative.InitiativeFilterDto;

import java.util.List;

public interface InitiativeService {
    InitiativeDto create(InitiativeDto initiative);

    InitiativeDto update(InitiativeDto initiative);

    List<InitiativeDto> getAllByFilter(InitiativeFilterDto filter);

    List<InitiativeDto> getAll();

    InitiativeDto getById(long id);
}
