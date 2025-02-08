package faang.school.projectservice.dto.meet;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * DTO for {@link faang.school.projectservice.model.Meet}
 */
@Builder
public record MeetFilterRequest(@Size(max = 128) String title,
                                @NotNull Long projectId,
                                LocalDateTime beforeDateTime) {
}