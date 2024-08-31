package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.StageStatus;
import faang.school.projectservice.model.stage.StageRoles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StageDto {
    private Long stageId;
    private String stageName;
    private Long projectId;
    private StageStatus status;
    private List<StageRoles> stageRoles;
}