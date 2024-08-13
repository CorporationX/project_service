package faang.school.projectservice.mapper;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import faang.school.projectservice.dto.calendar.EventDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {

    @Mapping(source = "start", target = "startDateTime", qualifiedByName = "dateTimeToLocalDateTime")
    @Mapping(source = "end", target = "endDateTime", qualifiedByName = "dateTimeToLocalDateTime")
    EventDto toDto(Event event);

    @Mapping(source = "startDateTime", target = "start", qualifiedByName = "localDateTimeToEventDateTime")
    @Mapping(source = "endDateTime", target = "end", qualifiedByName = "localDateTimeToEventDateTime")
    Event toEvent(EventDto eventDto);

    @Named("dateTimeToLocalDateTime")
    default LocalDateTime dateTimeToLocalDateTime(EventDateTime eventDateTime) {
        if (eventDateTime == null || eventDateTime.getDateTime() == null) {
            return null;
        }
        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(eventDateTime.getDateTime().getValue()),
                ZoneId.systemDefault());
    }

    @Named("localDateTimeToEventDateTime")
    default EventDateTime localDateTimeToEventDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        DateTime dateTime = new DateTime(Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant()));
        return new EventDateTime().setDateTime(dateTime).setTimeZone(TimeZone.getDefault().getID());
    }
}
