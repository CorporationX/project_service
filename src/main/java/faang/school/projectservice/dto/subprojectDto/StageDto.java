package faang.school.projectservice.dto.subprojectDto;

import faang.school.projectservice.model.stage.StageRoles;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StageDto {
    private Long stageID;
    @NotNull
    private String stageName;
    @NotNull
    private Long projectId;
    @NotNull
    private List<StageRoles> stageRoles;
}
