package faang.school.projectservice.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;

@Builder
public record TaskCreateRequest(

        @NotNull(message = "Name is missing")
        @NotBlank(message = "Name cannot be blank")
        @Size(min = 3, max = 50, message = "Name size must be between {min} and {max} symbols")
        String name,

        String description,

        @NotNull(message = "Id performer is missing")
        Long performerUserId,

        @NotNull(message = "Id reporter is missing")
        Long reporterUserId,

        Integer minutesTracked,

        Long parentTaskId,

        List<Long> linkedTasksIds,

        @NotNull(message = "Id project is missing")
        Long projectId
) {
}
