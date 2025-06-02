package faang.school.projectservice.dto.event;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateEventDto {
    @NotNull(message = "Event summary could not be null")
    private String summary;

    @NotNull(message = "Event description could not be null")
    private String description;

    @NotNull(message = "Start details could not be null")
    private EventDateTimeDto start;

    @NotNull(message = "End details could not be null")
    private EventDateTimeDto end;
}