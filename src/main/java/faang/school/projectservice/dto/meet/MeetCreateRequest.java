package faang.school.projectservice.dto.meet;

import faang.school.projectservice.model.MeetStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for {@link faang.school.projectservice.model.Meet}
 */
@Builder
public record MeetCreateRequest(@NotBlank(message = "Название встречи не может быть пустым")
                                @Size(max = 128, message = "Описание не больше 128 символов")
                                String title,

                                @NotBlank(message = "Описание встречи не может быть пустым")
                                @Size(max = 512, message = "Описание не больше 512 символов")
                                String description,

                                MeetStatus status,

                                @NotNull @Positive
                                Long creatorId,

                                @NotNull @Positive
                                Long projectId,

                                List<Long> userIds,

                                @NotNull(message = "Нет времени начала встречи")
                                LocalDateTime startsAt) {
}