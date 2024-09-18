package faang.school.projectservice.dto.project.stage;

import lombok.Builder;

@Builder
public record RemoveTypeDto(
        RemoveAction removeAction,
        Long stageForMigrateId) {
}
