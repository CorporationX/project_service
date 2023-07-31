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

    @NotEmpty(message = "Stage name can not be empty")
    private String stageName;

    @NotNull(message = "Project ID can not be null")
    private Long projectId;

    private String status;

    @NotNull(message = "List of roles can not be null")
    private List<StageRolesDto> stageRolesDto;
}