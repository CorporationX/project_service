package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.model.initiative.Initiative;

public interface MomentService {
    MomentDto create(MomentDto momentDto);

    MomentDto createFromInitiative(Initiative initiative);
}
