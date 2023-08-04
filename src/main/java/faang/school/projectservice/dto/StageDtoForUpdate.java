package faang.school.projectservice.dto;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.StageStatus;
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
@Schema(description = "Info about stage for update")
public class StageDtoForUpdate {

    @Schema(description = "Stage ID")
    @Min(1L)
    @Max(Long.MAX_VALUE)
    @NotNull(message = "Stage ID can not be null")
    private Long stageId;
    @Schema(description = "Stage author ID")
    @Min(1L)
    @Max(Long.MAX_VALUE)
    @NotNull(message = "Author ID can not be null")
    private Long authorId;
    @Schema(description = "Info about stage")
    private String stageName;
    @Schema(description = "Project ID")
    @Min(1L)
    @Max(Long.MAX_VALUE)
    @NotNull(message = "Project ID can not be null")
    private Long projectId;
    @Schema(description = "Stage status")
    private StageStatus status;
    @Schema(description = "List of team roles")
    @NotNull(message = "List of roles can not be null")
    @NotEmpty(message = "List of roles can not be empty")
    private List<TeamRole> teamRoles;
}