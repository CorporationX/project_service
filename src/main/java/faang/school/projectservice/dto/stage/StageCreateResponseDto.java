package faang.school.projectservice.dto.stage;

import faang.school.projectservice.dto.stage.stage_role.StageRolesCreateResponseDto;
import lombok.Data;

import java.util.List;

@Data
public class StageCreateResponseDto {
    private Long stageId;
    private String stageName;
    private Long projectId;
    private List<StageRolesCreateResponseDto> stageRolesDtos;
}
