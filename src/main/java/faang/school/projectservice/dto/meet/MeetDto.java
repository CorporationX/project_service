package faang.school.projectservice.dto.meet;

import faang.school.projectservice.dto.google.calendar.ZonedDateTimeDto;
import faang.school.projectservice.model.MeetStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
public class MeetDto {
    private long id;
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotNull
    private ZonedDateTimeDto startDate;
    @NotNull
    private ZonedDateTimeDto endDate;
    @NotNull
    private MeetStatus status;
    @NotNull
    private Long creatorId;
    @NotNull
    private long projectId;
    private String calendarEventId;
    private List<Long> userIds;
    private List<String> attendeeEmails;
}
