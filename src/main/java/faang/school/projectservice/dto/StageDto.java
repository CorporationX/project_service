package faang.school.projectservice.dto;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.stage.StageRoles;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class StageDto {
    private long stageId;
    @NotBlank(message = "Name should not be empty.")
    private String stageName;
    @NotNull(message = "Project must not be null")
    @Valid
    private Project project;
    private List<StageRoles> stageRoles;
    private List<Long> taskIds;
    private List<Long> executorIds;
}
