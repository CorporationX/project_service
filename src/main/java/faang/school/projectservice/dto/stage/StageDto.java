package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.StageRoles;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@Schema(description = "Stage dto")
public class StageDto {
    @NotNull(message = "Id cannot be null")
    @Min(value = 0, message = "Id must be > 0")
    @Schema(description = "Stage id")
    private Long stageId;
    @NotBlank(message = "Stage name must not be empty")
    @Schema(description = "stage name")
    private String stageName;
    @NotNull(message = "There must be a project for the stage")
    @Schema(description = "Project related id")
    private Project project;
    @Schema(description = "Stage available roles", accessMode = Schema.AccessMode.READ_ONLY)
    private List<StageRoles> stageRoles;
    @Schema(description = "Stage executors", accessMode = Schema.AccessMode.READ_ONLY)
    private List<TeamMember> executors;
    @Schema(description = "Stage tasks", accessMode = Schema.AccessMode.READ_ONLY)
    private List<Task> tasks;
}
