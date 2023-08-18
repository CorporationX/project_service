package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.stage.StageStatus;
import lombok.Data;

@Data
public class StageFilterDto {
    private StageStatus status;
}
