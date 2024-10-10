package faang.school.projectservice.mapper.calendar;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import faang.school.projectservice.model.dto.CalendarEventDto;
import faang.school.projectservice.model.enums.CalendarEventStatus;
import faang.school.projectservice.model.dto.ZonedDateTimeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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

    @Mapping(source = "start", target = "startDate", qualifiedByName = "toZonedDateTimeDto")
    @Mapping(source = "end", target = "endDate", qualifiedByName = "toZonedDateTimeDto")
    @Mapping(source = "status", target = "status", qualifiedByName = "toCalendarEventStatus")
    @Mapping(source = "attendees", target = "attendeeEmails", qualifiedByName = "fromEventAttendees")
    CalendarEventDto toDto(Event event);

    @Named("toEventDateTime")
    default EventDateTime toEventDateTime(ZonedDateTimeDto zonedDateTimeDto) {
        if (zonedDateTimeDto.getLocalDateTime() == null || zonedDateTimeDto.getTimeZone() == null) {
            return null;
        }
        return new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(
                        java.util.Date.from(
                                zonedDateTimeDto.getLocalDateTime()
                                        .atZone(ZoneId.of(zonedDateTimeDto.getTimeZone())).toInstant())))
                .setTimeZone(zonedDateTimeDto.getTimeZone());
    }

    @Named("toZonedDateTimeDto")
    default ZonedDateTimeDto toZonedDateTimeDto(EventDateTime eventDateTime) {
        if (eventDateTime == null || eventDateTime.getDateTime() == null) {
            return null;
        }
        ZonedDateTimeDto dateTimeDto = new ZonedDateTimeDto();
        dateTimeDto.setLocalDateTime(LocalDateTime.ofInstant(
                Instant.ofEpochMilli(eventDateTime.getDateTime().getValue()),
                ZoneId.of(eventDateTime.getTimeZone())
        ));
        dateTimeDto.setTimeZone(eventDateTime.getTimeZone());
        return dateTimeDto;
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
