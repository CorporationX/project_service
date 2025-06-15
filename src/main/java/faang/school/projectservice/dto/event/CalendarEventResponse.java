package faang.school.projectservice.dto.event;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Google calendar API create/update/delete event response")
public class CalendarEventResponse {
    @Schema(description = "Google calendar event internal id")
    private String id;
    @Schema(description = "Google calendar event title")
    private String summary;
    @Schema(description = "Google calendar event description")
    private String description;
    @Schema(description = "Google calendar event start dto with timezone")
    private EventDateTimeDto start;
    @Schema(description = "Google calendar event end dto with timezone")
    private EventDateTimeDto end;
    @Schema(description = "Google calendar response status", example = "confirmed")
    private String status;
}