package faang.school.projectservice.dto;

import faang.school.projectservice.model.stage.StageRoles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class StageDto {
    private Long stageId;
    private String stageName;
    private String projectName;
    private List<StageRoles> stageRoles;
}
