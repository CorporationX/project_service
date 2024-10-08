package faang.school.projectservice.dto.project.resource;

import faang.school.projectservice.model.ResourceType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigInteger;

public record ResourceDto(
        @NotNull(message = "Resource ID must not be null")
        @Positive(message = "Resource ID must be positive")
        Long id,

        @NotBlank( message = "Resource name must not be blank")
        @Size(max = 255, message = "Resource name must not exceed 255 characters")
        String name,

        @NotBlank(message = "Resource key must not be blank")
        @Size(max = 255, message = "Resource key must not exceed 255 characters")
        String key,

        @NotNull(message = "Resource size must not be null")
        @Positive(message = "Resource size must be positive")
        BigInteger size,

        @NotNull(message = "Resource type must not be null")
        ResourceType type
) {
}
