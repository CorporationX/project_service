package faang.school.projectservice.model.dto.moment;

import faang.school.projectservice.validator.groups.CreateGroup;
import faang.school.projectservice.validator.groups.UpdateGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record MomentDto(
        @Null(message = "Id must be null", groups = {CreateGroup.class, UpdateGroup.class})
        Long Id,

        @NotBlank(message = "Name can not be null or empty")
        @Size(max = 155)
        String name,

        @NotNull(message = "Description can not be null")
        @Size(max = 4096)
        String description,

        @NotNull(message = "Date can not be null")
        LocalDateTime date,

        @NotNull(message = "Projects List can not be null")
        List<Long> projectIds
) {
}

