package faang.school.projectservice.mapper;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import faang.school.projectservice.dto.client.EventDtoForGoogleCalendar;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {
    @Mapping(target = "summary", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "location", source = "location")
    @Mapping(target = "start", source = "startDate", qualifiedByName = "localDateTimeToEventDateTime")
    @Mapping(target = "end", source = "endDate", qualifiedByName = "localDateTimeToEventDateTime")
    @Mapping(target = "attendees", source = "attendeesEmails", qualifiedByName = "emailsToEventAttendees")
    com.google.api.services.calendar.model.Event mapToGoogleEvent(EventDtoForGoogleCalendar eventDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "title", source = "summary")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "location", source = "location")
    @Mapping(target = "startDate", source = "start", qualifiedByName = "eventDateTimeToLocalDateTime")
    @Mapping(target = "endDate", source = "end", qualifiedByName = "eventDateTimeToLocalDateTime")
    @Mapping(target = "attendeesEmails", source = "attendees", qualifiedByName = "eventAttendeesToEmails")
    EventDtoForGoogleCalendar mapToEventDto(com.google.api.services.calendar.model.Event googleEvent);

    @Named("localDateTimeToEventDateTime")
    default EventDateTime localDateTimeToEventDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        DateTime dateTime = new DateTime(zonedDateTime.toInstant().toEpochMilli());
        return new EventDateTime().setDateTime(dateTime);
    }

    @Named("eventDateTimeToLocalDateTime")
    default LocalDateTime eventDateTimeToLocalDateTime(EventDateTime eventDateTime) {
        if (eventDateTime == null || eventDateTime.getDateTime() == null) {
            return null;
        }
        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(eventDateTime.getDateTime().getValue()),
                ZoneId.systemDefault()
        );
    }

    @Named("emailsToEventAttendees")
    default List<EventAttendee> emailsToEventAttendees(List<String> emails) {
        if (emails == null) {
            return null;
        }
        return emails.stream()
                .map(email -> new EventAttendee().setEmail(email))
                .collect(Collectors.toList());
    }

    @Named("eventAttendeesToEmails")
    default List<String> eventAttendeesToEmails(List<EventAttendee> attendees) {
        if (attendees == null) {
            return null;
        }
        return attendees.stream()
                .map(EventAttendee::getEmail)
                .collect(Collectors.toList());
    }
}
