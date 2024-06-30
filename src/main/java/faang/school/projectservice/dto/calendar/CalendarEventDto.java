package faang.school.projectservice.dto.calendar;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.projectservice.exceptions.DataValidationException;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CalendarEventDto {
    private String id;
    private String summary;
    private String description;
    private String location;
    @NotNull(message = "Start time is required to create an event.")
    @Future(message = "You can't create event in the past.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;
    @NotNull(message = "End time is required to create an event.")
    @Future(message = "You can't create event in the past.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;

    public void verifyEndIsAfterStartTime() {
        if (endTime.isBefore(startTime)) {
            throw new DataValidationException("End time must be after start time");
        }
    }
}