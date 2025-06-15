package faang.school.projectservice.dto.event;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Create google calendar API event")
public class CreateEventDto {
    @NotBlank(message = "Event summary could not be null")
    @Schema(description = "Google calendar event title")
    private String summary;

    @NotBlank(message = "Event description could not be null")
    @Schema(description = "Google calendar event description")
    private String description;

    @NotNull(message = "Start details could not be null")
    @Schema(description = "Google calendar event start dto with timezone")
    private EventDateTimeDto start;

    @NotNull(message = "End details could not be null")
    @Schema(description = "Google calendar event end dto with timezone")
    private EventDateTimeDto end;
}