package faang.school.projectservice.dto.google.calendar;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CalendarEventDto {
    private String id;
    @NotBlank
    private String summary;
    @NotBlank
    private String description;
    @NotNull
    private LocalDateTime startDate;
    @NotNull
    private LocalDateTime endDate;
    @NotNull
    private String timeZone;
    private List<String> attendeeEmails;
    private CalendarEventStatus status;
    private String location;
}
