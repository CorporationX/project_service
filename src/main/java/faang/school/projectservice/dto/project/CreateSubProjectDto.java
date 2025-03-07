package faang.school.projectservice.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateSubProjectDto(
        @NotBlank (message = "Name must not be blank") String name,
        String description,
        @NotNull (message = "Owner Id must not be null")
        @Positive(message = "Owner Id must be positive number") Long ownerId,
        @NotNull (message = "Parent Project Id must not be null")
        @Positive(message = "Parent Project Id must be positive number") Long parentProjectId) {
}
