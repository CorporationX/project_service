package faang.school.projectservice.dto.stage;

import faang.school.projectservice.dto.stage.stage_role.StageRolesUpdateRequestDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class StageUpdateRequestDto {
    @Positive
    private Long stageId;
    @NotEmpty
    private List<StageRolesUpdateRequestDto> stageRolesDtos;
    @NotEmpty
    private List<Long> teamMemberIds;
    @Positive
    private Long stageUpdateAuthorId;
}