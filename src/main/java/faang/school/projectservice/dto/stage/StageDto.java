package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.stage.StageRoles;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StageDto {
    private Long stageId;
    @NotBlank
    private String stageName;
    @NotNull
    private Long projectId;
    //private ProjectStatus projectStatus;
    @NotNull
    private List<StageRoles> stageRoles;
    //@NotNull
    //private List<Integer> countExecutorsForRole;
    private List<Long> taskIds;
    private List<Long> executorIds;
}
