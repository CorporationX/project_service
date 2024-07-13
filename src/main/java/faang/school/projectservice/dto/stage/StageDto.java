package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.StageStatus;
import faang.school.projectservice.model.stage.StageRoles;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class StageDto {
    private Long stageId;
    private String stageName;
    private Long projectId;
    private StageStatus status;
    private List<StageRoles> stageRoles;
}