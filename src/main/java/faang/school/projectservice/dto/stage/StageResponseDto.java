package faang.school.projectservice.dto.stage;

import faang.school.projectservice.dto.stage.stage_role.StageRolesResponseDto;
import lombok.Data;

import java.util.List;

@Data
public class StageResponseDto {
    private Long stageId;
    private String stageName;
    private Long projectId;
    private List<StageRolesResponseDto> stageRolesDtos;
}
