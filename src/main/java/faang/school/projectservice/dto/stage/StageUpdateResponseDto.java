package faang.school.projectservice.dto.stage;

import faang.school.projectservice.dto.stage.stage_role.StageRolesUpdateResponseDto;
import lombok.Data;

import java.util.List;

@Data
public class StageUpdateResponseDto {
    private Long stageId;
    private String stageName;
    private Long projectId;
    private List<StageRolesUpdateResponseDto> stageRolesDtos;
    private List<Long> teamMemberIds;
}
