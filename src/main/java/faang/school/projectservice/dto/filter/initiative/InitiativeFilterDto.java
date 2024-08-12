package faang.school.projectservice.dto.filter.initiative;

import faang.school.projectservice.model.initiative.InitiativeStatus;

public record InitiativeFilterDto(InitiativeStatus status, Long curatorId) {
}
