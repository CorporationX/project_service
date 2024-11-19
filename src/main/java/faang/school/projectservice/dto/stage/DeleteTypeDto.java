package faang.school.projectservice.dto.stage;

import faang.school.projectservice.entity.stage.DeleteStrategy;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeleteTypeDto {
    DeleteStrategy getDeleteStrategy;
    DeleteStrategy deleteStrategy;
    Long stageForMigrateId;
}