package faang.school.projectservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
public class StageDto {
    private Long stageId;
    private String stageName;
    private Long projectId;
    private List<Long> stageRoleIds;
    private List<Long> teamMemberIds;
}

