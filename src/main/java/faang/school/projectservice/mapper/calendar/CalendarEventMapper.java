package faang.school.projectservice.mapper.calendar;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import faang.school.projectservice.dto.google.calendar.CalendarEventDto;
import faang.school.projectservice.dto.google.calendar.CalendarEventStatus;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CalendarEventMapper {

    @Mapping(source = "startDate", target = "start", qualifiedByName = "toEventDateTime")
    @Mapping(source = "endDate", target = "end", qualifiedByName = "toEventDateTime")
    @Mapping(source = "status", target = "status", qualifiedByName = "statusToLowerCase")
    @Mapping(source = "attendeeEmails", target = "attendees", qualifiedByName = "toEventAttendees")
    Event toEvent(CalendarEventDto dto);

    @Mapping(source = "start", target = "startDate", qualifiedByName = "toLocalDateTime")
    @Mapping(source = "end", target = "endDate", qualifiedByName = "toLocalDateTime")
    @Mapping(source = "status", target = "status", qualifiedByName = "toCalendarEventStatus")
    @Mapping(source = "attendees", target = "attendeeEmails", qualifiedByName = "fromEventAttendees")
    CalendarEventDto toDto(Event event);

    @Named("toEventDateTime")
    default EventDateTime toEventDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(java.util.Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())));
    }

    @AfterMapping
    default void setEventTimeZone(@MappingTarget Event event, CalendarEventDto dto) {
        if (dto.getTimeZone() != null && event.getStart() != null) {
            event.getStart().setTimeZone(dto.getTimeZone());
            event.getEnd().setTimeZone(dto.getTimeZone());
        }
    }

    @Named("toLocalDateTime")
    default LocalDateTime toLocalDateTime(EventDateTime eventDateTime) {
        if (eventDateTime == null || eventDateTime.getDateTime() == null) {
            return null;
        }
        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(eventDateTime.getDateTime().getValue()),
                ZoneId.of(eventDateTime.getTimeZone())
        );
    }

    @Named("statusToLowerCase")
    default String statusToLowerCase(CalendarEventStatus status) {
        return status == null ? null : status.toString().toLowerCase();
    }

    @Named("toCalendarEventStatus")
    default CalendarEventStatus toCalendarEventStatus(String status) {
        if (status == null) {
            return null;
        }
        return CalendarEventStatus.valueOf(status.toUpperCase());
    }

    @Named("toEventAttendees")
    default List<EventAttendee> toEventAttendees(List<String> attendeeEmails) {
        if (attendeeEmails == null || attendeeEmails.isEmpty()) {
            return null;
        }
        return attendeeEmails.stream()
                .map(email -> new EventAttendee().setEmail(email))
                .collect(Collectors.toList());
    }

    @Named("fromEventAttendees")
    default List<String> fromEventAttendees(List<EventAttendee> attendees) {
        if (attendees == null || attendees.isEmpty()) {
            return null;
        }
        return attendees.stream()
                .map(EventAttendee::getEmail)
                .collect(Collectors.toList());
    }
}
