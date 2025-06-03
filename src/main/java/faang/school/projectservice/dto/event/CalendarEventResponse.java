package faang.school.projectservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalendarEventResponse {
    private String id;
    private String summary;
    private String description;
    private EventDateTimeDto start;
    private EventDateTimeDto end;
    private String status;
}