package faang.school.projectservice.dto.stage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

    @NotBlank
    private String stageName;

    @Positive
    @NotNull
    private Long projectId;

    @NotNull
    private List<Long> stageRolesIds;

    @NotNull
    private List<Long> tasksIds;

    @NotNull
    private List<Long> executorsIds;
}