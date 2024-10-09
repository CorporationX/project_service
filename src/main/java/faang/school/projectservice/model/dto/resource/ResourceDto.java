package faang.school.projectservice.model.dto.resource;

import faang.school.projectservice.model.entity.ResourceStatus;
import faang.school.projectservice.model.entity.ResourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigInteger;

@Builder
public record ResourceDto(
        Long id,

        @NotBlank(message = "Resource name cannot be empty")
        String name,

        @Positive(message = "Resource size must be bigger than 0")
        BigInteger size,

        @Positive(message = "Resource's project cannot be empty")
        Long projectId,

        @NotNull(message = "Resource type must be chosen")
        ResourceType type,

        @NotNull(message = "Resource status must be chosen")
        ResourceStatus status
) {
}
