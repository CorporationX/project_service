package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.StageStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StageFilterDto {
    private StageStatus status;
}
