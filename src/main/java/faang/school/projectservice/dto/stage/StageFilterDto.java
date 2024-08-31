package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.StageStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StageFilterDto {
    private StageStatus status;
}
