package faang.school.projectservice.model.dto;

import faang.school.projectservice.model.enums.MeetStatus;
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
