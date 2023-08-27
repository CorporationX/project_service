package faang.school.projectservice.dto.stage;

import faang.school.projectservice.dto.stage_roles.StageRolesDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Info about stage")
public class StageDto {
    @Schema(description = "ID of stage")
    private Long stageId;
    @Schema(description = "Name of stage")
    @NotEmpty(message = "Stage name can not be empty")
    private String stageName;
    @Schema(description = "ID of project")
    @Min(1L)
    @Max(Long.MAX_VALUE)
    @NotNull(message = "Project ID can not be null")
    private Long projectId;
    @Schema(description = "Status of stage")
    @NotEmpty(message = "Status can not be null")
    private String status;
    @Schema(description = "List of stage roles")
    @NotNull(message = "List of roles can not be null")
    private List<StageRolesDto> stageRolesDto;
}