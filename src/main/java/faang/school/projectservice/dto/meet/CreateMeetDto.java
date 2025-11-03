package faang.school.projectservice.dto.meet;

import faang.school.projectservice.validation.ValidationConstants;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record CreateMeetDto(
        @NotBlank(message = "Title must not be blank")
        @Size(max = ValidationConstants.TITLE_MAX_LENGTH, message = ValidationConstants.TITLE_SIZE_MESSAGE)
        String title,

        @NotBlank(message = "Description must not be blank")
        @Size(max = ValidationConstants.DESCRIPTION_MAX_LENGTH, message = ValidationConstants.DESCRIPTION_SIZE_MESSAGE)
        String description,

        @NotNull(message = "Project id should be present")
        Long projectId,

        @NotEmpty(message = "Users ids list cant be empty")
        List<Long> userIds,

        @NotNull(message = "Start date must not be null")
        @Future(message = "Start date must be in future")
        LocalDateTime startsAt
) {
}