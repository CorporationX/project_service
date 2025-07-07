package faang.school.projectservice.mapper.google.calendar;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.EventDateTime;
import faang.school.projectservice.dto.google.calendar.GoogleCalendarEventDto;
import com.google.api.services.calendar.model.Event;
import faang.school.projectservice.model.google.calendar.GoogleCalendarEvent;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface GoogleEventMapper {

    @Mapping(target = "summary", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "start", expression = "java(toGoogleDateTime(dto.getStartTime(), dto.getLocation()))")
    @Mapping(target = "end", expression = "java(toGoogleDateTime(dto.getEndTime(), dto.getLocation()))")
    Event toGoogleEvent(GoogleCalendarEventDto dto);

    @Mapping(target = "summary", source = "title",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "description", source = "description",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateGoogleEventFromDto(GoogleCalendarEventDto dto, @MappingTarget Event event);

    @Mapping(target = "googleCalendarId", source = "id")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "title", source = "summary")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "startTime", expression = "java(toLocalDateTime(event.getStart()))")
    @Mapping(target = "endTime", expression = "java(toLocalDateTime(event.getEnd()))")
    @Mapping(target = "url", source = "htmlLink")
    GoogleCalendarEvent toEntity(Event event);

    @AfterMapping
    default void afterMapping(GoogleCalendarEventDto dto, @MappingTarget Event event) {
        if (dto.getStartTime() != null && dto.getLocation() != null) {
            event.setStart(toGoogleDateTime(dto.getStartTime(), dto.getLocation()));
        }
        if (dto.getEndTime() != null && dto.getLocation() != null) {
            event.setEnd(toGoogleDateTime(dto.getEndTime(), dto.getLocation()));
        }
    }

    default EventDateTime toGoogleDateTime(LocalDateTime dateTime, String zone) {
        if (dateTime == null || zone == null) return null;
        ZonedDateTime zdt = dateTime.atZone(ZoneId.of(zone));
        DateTime googleDateTime = new DateTime(zdt.toInstant().toEpochMilli());
        return new EventDateTime().setDateTime(googleDateTime).setTimeZone(zone);
    }

    default LocalDateTime toLocalDateTime(EventDateTime eventDateTime) {
        if (eventDateTime == null || eventDateTime.getDateTime() == null) {
            return null;
        }
        Instant instant = Instant.ofEpochMilli(eventDateTime.getDateTime().getValue());
        return LocalDateTime.ofInstant(instant, ZoneId.of(eventDateTime.getTimeZone() != null ? eventDateTime.getTimeZone() : "UTC"));
    }
}