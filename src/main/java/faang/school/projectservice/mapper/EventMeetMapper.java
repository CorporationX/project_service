package faang.school.projectservice.mapper;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import faang.school.projectservice.dto.MeetDto;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.service.ProjectService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Value;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {ProjectService.class}
)
public interface EventMeetMapper {

    int eventPopUpTime = 10;
    int eventEmailNotifyTime = 25 * 60;

    @Mapping(source = "summary", target = "name")
    @Mapping(target = "startDateTime", ignore = true)
    @Mapping(target = "endDateTime", ignore = true)
    @Mapping(source = "id", target = "eventGoogleId")
    @Mapping(source = "htmlLink", target = "eventGoogleUrl")
    @Mapping(source = "end.timeZone", target = "timeZone")
    MeetDto eventToMeetDto(Event event);

    @Mapping(source = "name", target = "summary")
    @Mapping(target = "start", expression = "java(getDateTime(meetDto.getStartDateTime(), meetDto.getTimeZone()))")
    @Mapping(target = "end", expression = "java(getDateTime(meetDto.getEndDateTime(), meetDto.getTimeZone()))")
    @Mapping(target = "reminders", expression = "java(getReminders())")
    Event meetDtoToEvent(MeetDto meetDto);

    @Mapping(source = "projectId", target = "project")
    Meet toMeetEntity(MeetDto meetDto);

    @Mapping(source = "project.id", target = "projectId")
    MeetDto toDto(Meet meet);

    @Mapping(source = "projectId", target = "project")
    void update(MeetDto meetDto, @MappingTarget Meet foundMeet);

    List<MeetDto> toDtoList(List<Meet> meetings);

    default EventDateTime getDateTime(ZonedDateTime zonedDateTime, String timeZone) {
        DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        DateTime dateTime = new DateTime(zonedDateTime.format(FORMATTER));
        return new EventDateTime()
                .setDateTime(dateTime)
                .setTimeZone(timeZone);
    }

    default Event.Reminders getReminders() {
        EventReminder[] reminderOverrides = new EventReminder[]{
                new EventReminder().setMethod("email").setMinutes(eventEmailNotifyTime),
                new EventReminder().setMethod("popup").setMinutes(eventPopUpTime),
        };
        return new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
    }
}
