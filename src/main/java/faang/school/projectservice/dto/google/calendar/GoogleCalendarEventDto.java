package faang.school.projectservice.dto.google.calendar;

import faang.school.projectservice.model.google.calendar.GoogleCalendar;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GoogleCalendarEventDto {
    private Long id;
    @NotBlank
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
