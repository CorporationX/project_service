package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.stage.StageRoles;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class StageDto {
    @Positive
    private Long stageId;

    @NotNull
    @NotBlank(message = "Stage name required")
    private String stageName;

    @Positive
    private Long projectId;

    @NotNull
    @NotEmpty(message = "Stage roles required")
    private List<@Valid StageRolesDto> stageRoles;
}
