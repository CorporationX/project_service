package faang.school.projectservice.dto.project.resource;

import faang.school.projectservice.model.ResourceType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigInteger;

public record ResourceDto(
        Long id,
        String name,
        String key,
        BigInteger size,
        ResourceType type
) {
}
