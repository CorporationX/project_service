package faang.school.projectservice.dto;

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
public class StageDto {
    private Long stageId;
    @NotEmpty
    private String stageName;
    @NotNull
    private Long projectId;
    private String status;
    @NotNull
    private List<StageRolesDto> stageRolesDto;
}
