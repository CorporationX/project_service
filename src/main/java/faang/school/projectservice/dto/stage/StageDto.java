package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.StageRoles;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StageDto {
    @NotNull (message = "Id cannot be null")
    @Min(value = 0,message = "Id must be > 0")
    private Long stageId;
    @NotBlank (message = "Stage name must not be empty")
    private String stageName;
    @NotNull (message = "There must be a project for the stage")
    private Project project;
    private List<StageRoles> stageRoles;
    private List<TeamMember> executors;
    private List<Task> tasks;
}
