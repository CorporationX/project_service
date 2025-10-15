package faang.school.projectservice.dto.project;

import jakarta.validation.constraints.NotNull;

public record ProjectCreateDto(
        @NotNull
        String name,

        @NotNull
        String description,

        @NotNull
        Long ownerId
) {
}
