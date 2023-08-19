package faang.school.projectservice.dto.stage;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StageDto {
    private Long stageId;
    private String stageName;
    private Long projectId;
    private List<Long> stageRoleIds;
    private List<Long> teamMemberIds;
    private List<Long> taskIds;
}
