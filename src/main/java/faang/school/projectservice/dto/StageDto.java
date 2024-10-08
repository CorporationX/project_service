package faang.school.projectservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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

    @NotNull(message = "Stage ID must not be null")
    private Long stageId;

    @NotBlank(message = "Stage name must not be blank")
    @Size(max = 255, message = "Stage name must not exceed 255 characters")
    private String stageName;

    @NotNull(message = "Project ID must not be null")
    @Positive(message = "Project ID must be a positive number")
    private Long projectId;

    @NotNull(message = "Stage roles must not be null")
    private List<StageRoleDto> stageRoles;

    private List<Long> teamMemberIds;
}