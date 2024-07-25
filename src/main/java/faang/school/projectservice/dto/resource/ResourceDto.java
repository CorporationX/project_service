package faang.school.projectservice.dto.resource;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigInteger;

public record ResourceDto(
        Long id,
        String name,
        @NotNull
        @Min(0)
        BigInteger size,
        Long projectId
) {
}
