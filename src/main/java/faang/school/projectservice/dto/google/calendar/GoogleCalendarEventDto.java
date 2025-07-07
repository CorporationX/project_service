package faang.school.projectservice.dto.google.calendar;

import faang.school.projectservice.validation.google.calendar.ValidationGroups;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GoogleCalendarEventDto {

    private Long id;

    @NotBlank(groups = ValidationGroups.OnCreate.class)
    private String title;

    @NotNull(groups = ValidationGroups.OnCreate.class)
    private String description;

    @NotNull(groups = ValidationGroups.OnCreate.class)
    private String location;

    @NotNull(groups = ValidationGroups.OnCreate.class)
    private LocalDateTime startTime;

    @NotNull(groups = ValidationGroups.OnCreate.class)
    private LocalDateTime endTime;

    @AssertTrue(message = "Start date must be before end date")
    public boolean isValidDateRange() {
        if (startTime == null || endTime == null) return true;
        return startTime.isBefore(endTime);
    }

    @AssertTrue(groups = ValidationGroups.OnUpdate.class)
    public boolean isUpdatedTitleBlank() {
        return  (title != null && !title.isBlank());
    }

    @AssertTrue(groups = ValidationGroups.OnUpdate.class)
    public boolean isUpdatedDescriptionBlank() {
        return  (description != null && !description.isBlank());
    }

    @AssertTrue(groups = ValidationGroups.OnUpdate.class)
    public boolean isUpdatedLocationBlank() {
        return  (location != null && !location.isBlank());
    }
}
