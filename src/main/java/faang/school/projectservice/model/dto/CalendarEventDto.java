package faang.school.projectservice.model.dto;

import faang.school.projectservice.model.enums.CalendarEventStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

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
    private ZonedDateTimeDto startDate;
    @NotNull
    private ZonedDateTimeDto endDate;
    private List<String> attendeeEmails;
    private CalendarEventStatus status;
    private String location;
}
