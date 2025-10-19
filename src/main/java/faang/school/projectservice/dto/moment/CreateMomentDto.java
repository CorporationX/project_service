package faang.school.projectservice.dto.moment;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record CreateMomentDto(
        @NotBlank(message = "The field name cannot be empty")
        @Size(max = 255, message = "Name cannot exceed 255 characters")
        String name,
        @Size(max = 255, message = "Description cannot exceed 255 characters")
        String description,
        @NotEmpty(message = "There must be at least one project")
        List<@NotNull Long> projectIds,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime date
) {
}
