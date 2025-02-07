package faang.school.projectservice.dto.stage;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "The stageName field must not be null or blank!")
    private String stageName;
    @NotNull(message = "The projectId field cannot be null!")
    @Min(value = 1, message = "The projectId cannot be less than or equal to zero!")
    private long projectId;
    @NotNull(message = "The userId field cannot be null!")
    @Min(value = 1, message = "The userId cannot be less than or equal to zero!")
    private long userId;
    @NotNull(message = "The stageRoles field cannot be null!")
    @NotEmpty(message = "The stageRoles field cannot be empty!")
    private List<StageRolesDto> stageRoles;
}
