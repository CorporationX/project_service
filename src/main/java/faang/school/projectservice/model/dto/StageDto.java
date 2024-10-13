package faang.school.projectservice.model.dto;

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

    private Long stageId;

    @NotBlank(message = "Stage name must not be blank")
    @Size(max = 255, message = "Stage name must be less than or equal to 255 characters")
    private String stageName;

    @Positive(message = "Project ID must be a positive number")
    private Long projectId;

    @NotNull(message = "Stage roles must not be null")
    private List<StageRoleDto> stageRoles;

    private List<Long> teamMemberIds;
}