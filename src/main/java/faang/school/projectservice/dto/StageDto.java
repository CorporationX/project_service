package faang.school.projectservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StageDto {
    private Long stageId;

    @NotBlank(message = "Stage name cannot be null or blank")
    private String stageName;

    @NotNull(message = "The stage must belong to some project")
    private Long projectId;

    @Valid
    @NotEmpty(message = "Stage roles cannot be empty")
    private List<StageRolesDto> stageRoles;
}

