package faang.school.projectservice.dto.meet;

import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.validation.ValidationConstants;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record UpdateMeetDto(

        @Size(max = ValidationConstants.TITLE_MAX_LENGTH, message = ValidationConstants.TITLE_SIZE_MESSAGE)
        String title,

        @Size(max = ValidationConstants.DESCRIPTION_MAX_LENGTH, message = ValidationConstants.DESCRIPTION_SIZE_MESSAGE)
        String description,

        MeetStatus status,
        Long projectId,
        List<Long> userIds,

        @Future(message = "Start date must be in future")
        LocalDateTime startsAt
) {
}