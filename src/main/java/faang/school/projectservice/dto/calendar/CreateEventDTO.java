package faang.school.projectservice.dto.calendar;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
public class CreateEventDTO {

    @NotBlank(message = "Summary is required")
    private String summary;

    private String description;

    @NotBlank(message = "Start date/time is required")
    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}([+-]\\d{2}:\\d{2}|Z)$",
            message = "StartDateTime must be in ISO 8601 format (e.g., 2024-02-02T10:00:00+06:00 or 2024-02-02T04:00:00Z)"
    )
    private String startDateTime;

    @NotBlank(message = "End date/time is required")
    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}([+-]\\d{2}:\\d{2}|Z)$",
            message = "EndDateTime must be in ISO 8601 format (e.g., 2024-02-02T12:00:00+06:00 or 2024-02-02T06:00:00Z)"
    )
    private String endDateTime;
}

