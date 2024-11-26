package faang.school.projectservice.dto.stage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Validated
public class StageDto {
    @NotBlank(message = "Stage name is required")
    @Size(min = 3, max = 50, message = "Stage name must be between 3 and 50 characters")
    private String stageName;

    @NotNull(message = "Project id is required")
    private Long projectId;

    @NotNull(message = "Stage roles are required")
    private List<StageRolesDto> stageRoles;
}
