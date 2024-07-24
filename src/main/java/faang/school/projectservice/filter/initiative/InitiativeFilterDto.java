package faang.school.projectservice.filter.initiative;

import faang.school.projectservice.model.initiative.InitiativeStatus;
import lombok.Value;

@Value
public class InitiativeFilterDto {

    InitiativeStatus status;
    Long curatorId;
}
