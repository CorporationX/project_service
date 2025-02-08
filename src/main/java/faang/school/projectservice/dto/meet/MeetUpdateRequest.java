package faang.school.projectservice.dto.meet;

import faang.school.projectservice.model.MeetStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * DTO for {@link faang.school.projectservice.model.Meet}
 */
@Builder
public record MeetUpdateRequest(@NotNull Long userId,
                                @NotNull Long meetId,
                                @Size(max = 128) String title,
                                @Size(max = 512) String description,
                                MeetStatus status,
                                Long projectId,
                                LocalDateTime startsAt) {
}