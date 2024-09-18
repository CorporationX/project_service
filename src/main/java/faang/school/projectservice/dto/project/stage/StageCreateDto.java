package faang.school.projectservice.dto.project.stage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.List;

@Builder
public record StageCreateDto(

        @NotBlank(message = "Stage name is required, and can't be blank")
        String stageName,

        @NotNull(message = "Project id is required")
        @Positive(message = "Project id must be positive")
        Long projectId,

        @NotEmpty(message = "Stage roles is required")
        List<StageRoleDto> roles) {
}
