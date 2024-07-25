package faang.school.projectservice.dto.stage;

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
    @NotNull
    private String stageName;
    @NotNull
    private Long projectId;
    @NotEmpty
    private List<StageRolesDto> stageRolesDtosList;
}
