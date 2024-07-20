package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.stage.StageRoles;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

    @Positive
    private Long projectId;

    @NotNull
    private List<StageRoles> stageRoles;

    private List<Long> executorIds;
}
