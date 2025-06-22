package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.google.calendar.GoogleCalendarEventDto;
import faang.school.projectservice.model.google.calendar.GoogleCalendarEvent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GoogleCalendarEventMapper {
    GoogleCalendarEvent toEntity(GoogleCalendarEventDto googleCalendarEventDto);
    GoogleCalendarEventDto toDto(GoogleCalendarEvent googleCalendarEvent);
}
