package faang.school.projectservice.model.dto.meet;

import faang.school.projectservice.model.entity.meet.MeetStatus;
import faang.school.projectservice.validator.meet.CreateMeet;
import faang.school.projectservice.validator.meet.UpdateMeet;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record MeetRequestDto(
        @NotNull(message = "Meet id must not be null", groups = {UpdateMeet.class})
        Long id,
        @NotBlank(message = "Title must not be blank", groups = {CreateMeet.class, UpdateMeet.class})
        String title,
        @NotBlank(message = "Title must not be blank", groups = {CreateMeet.class, UpdateMeet.class})
        String description,
        @NotNull(message = "Status must not be null", groups = {CreateMeet.class})
        MeetStatus status,
        @NotNull
        Long projectId,
        List<Long> userIds
) {
}