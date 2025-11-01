package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.springframework.lang.Nullable;

@Builder
public record SearchDto(
        @Size(max = 1000, message = "Description search text must not exceed 1000 characters")
        @Nullable String description,

        @Nullable TeamRole position
) {
}
