package faang.school.projectservice.dto.calendar;

import faang.school.projectservice.model.MeetStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CalendarEventDto {
    Long id;
    Long creatorId;
    String calendarId;
    String title;
    String description;
    LocalDateTime startsAt;
    LocalDateTime endsAt;
}
