package faang.school.projectservice.mapper;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import faang.school.projectservice.dto.calendar.CalendarEventDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CalendarEventMapper {
    @Mapping(source = "startTime", target = "start", qualifiedByName = "eventDateMapping")
    @Mapping(source = "endTime", target = "end", qualifiedByName = "eventDateMapping")
    Event toModel(CalendarEventDto event);

    @Mapping(source = "start", target = "startTime", qualifiedByName = "timeMapping")
    @Mapping(source = "end", target = "endTime", qualifiedByName = "timeMapping")
    CalendarEventDto toDto(Event event);

    @Named("eventDateMapping")
    default EventDateTime eventDateMapping(LocalDateTime date) {
        DateTime dateTime = new DateTime(ZonedDateTime.of(date, ZoneId.systemDefault()).toInstant().toEpochMilli());
        return new EventDateTime().setDateTime(dateTime);
    }

    @Named("timeMapping")
    default LocalDateTime timeMapping(EventDateTime date) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getDateTime().getValue()), ZoneId.systemDefault());
    }

    List<CalendarEventDto> toDtos(List<Event> eventsOfProject);
}
