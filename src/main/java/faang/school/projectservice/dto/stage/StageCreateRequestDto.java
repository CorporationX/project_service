package faang.school.projectservice.dto.stage;

import faang.school.projectservice.dto.stage.stage_role.StageRolesCreateRequestDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class StageCreateRequestDto {
    @NotBlank
    private String stageName;
    @Positive
    private Long projectId;
    @NotEmpty
    private List<StageRolesCreateRequestDto> stageRolesDtos;
}
