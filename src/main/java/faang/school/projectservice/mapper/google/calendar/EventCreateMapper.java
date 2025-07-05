package faang.school.projectservice.mapper.google.calendar;

import faang.school.projectservice.dto.google.calendar.GoogleCalendarEventDto;
import faang.school.projectservice.model.google.calendar.GoogleCalendarEvent;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventCreateMapper {
    GoogleCalendarEvent toEntity(GoogleCalendarEventDto googleCalendarEventDto);

    GoogleCalendarEventDto toDto(GoogleCalendarEvent googleCalendarEvent);

    List<GoogleCalendarEventDto> toDtoList(List<GoogleCalendarEvent> events);
}