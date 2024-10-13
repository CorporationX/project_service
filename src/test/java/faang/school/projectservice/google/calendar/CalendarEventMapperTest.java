package faang.school.projectservice.google.calendar;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import faang.school.projectservice.model.dto.CalendarEventDto;
import faang.school.projectservice.model.enums.CalendarEventStatus;
import faang.school.projectservice.model.dto.ZonedDateTimeDto;
import faang.school.projectservice.mapper.calendar.CalendarEventMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CalendarEventMapperTest {

    private CalendarEventMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(CalendarEventMapper.class);
    }

    @Test
    void testToEvent() {
        CalendarEventDto dto = new CalendarEventDto();
        dto.setId("event-id");
        dto.setSummary("Test Event");
        dto.setDescription("This is a test event");
        dto.setLocation("Test Location");
        dto.setStartDate(new ZonedDateTimeDto(LocalDateTime.of(2024, 9, 30, 10, 0), "UTC"));
        dto.setEndDate(new ZonedDateTimeDto(LocalDateTime.of(2024, 9, 30, 12, 0), "UTC"));
        dto.setStatus(CalendarEventStatus.CONFIRMED);
        dto.setAttendeeEmails(Arrays.asList("attendee1@example.com", "attendee2@example.com"));

        Event event = mapper.toEvent(dto);

        assertThat(event).isNotNull();
        assertThat(event.getSummary()).isEqualTo(dto.getSummary());
        assertThat(event.getDescription()).isEqualTo(dto.getDescription());
        assertThat(event.getLocation()).isEqualTo(dto.getLocation());

        long expectedStartTime = java.util.Date.from(dto.getStartDate().getLocalDateTime().atZone(ZoneId.of("UTC")).toInstant()).getTime();
        assertThat(event.getStart().getDateTime().getValue()).isEqualTo(expectedStartTime);

        long expectedEndTime = java.util.Date.from(dto.getEndDate().getLocalDateTime().atZone(ZoneId.of("UTC")).toInstant()).getTime();
        assertThat(event.getEnd().getDateTime().getValue()).isEqualTo(expectedEndTime);

        assertThat(event.getAttendees()).hasSize(2);
        assertThat(event.getAttendees().get(0).getEmail()).isEqualTo("attendee1@example.com");
        assertThat(event.getAttendees().get(1).getEmail()).isEqualTo("attendee2@example.com");
        assertThat(event.getStatus()).isEqualTo(dto.getStatus().toString().toLowerCase());
    }

    @Test
    void testToDto() {
        Event event = new Event();
        event.setId("event-id");
        event.setSummary("Test Event");
        event.setDescription("This is a test event");
        event.setLocation("Test Location");

        EventDateTime start = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(java.util.Date.from(LocalDateTime.of(2024, 9, 30, 10, 0)
                        .atZone(ZoneId.of("UTC")).toInstant())))
                .setTimeZone("UTC");
        event.setStart(start);

        EventDateTime end = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(java.util.Date.from(LocalDateTime.of(2024, 9, 30, 12, 0)
                        .atZone(ZoneId.of("UTC")).toInstant())))
                .setTimeZone("UTC");
        event.setEnd(end);

        event.setAttendees(Arrays.asList(
                new EventAttendee().setEmail("attendee1@example.com"),
                new EventAttendee().setEmail("attendee2@example.com")
        ));
        event.setStatus("confirmed");

        CalendarEventDto dto = mapper.toDto(event);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(event.getId());
        assertThat(dto.getSummary()).isEqualTo(event.getSummary());
        assertThat(dto.getDescription()).isEqualTo(event.getDescription());
        assertThat(dto.getLocation()).isEqualTo(event.getLocation());
        assertThat(dto.getStartDate()).isEqualTo(new ZonedDateTimeDto(LocalDateTime.of(2024, 9, 30, 10, 0), "UTC"));
        assertThat(dto.getEndDate()).isEqualTo(new ZonedDateTimeDto(LocalDateTime.of(2024, 9, 30, 12, 0), "UTC"));
        assertThat(dto.getAttendeeEmails()).hasSize(2);
        assertThat(dto.getAttendeeEmails()).containsExactly("attendee1@example.com", "attendee2@example.com");
        assertThat(dto.getStatus()).isEqualTo(CalendarEventStatus.CONFIRMED);
    }

    @Test
    void testToEventAttendees() {
        List<String> attendeeEmails = Arrays.asList("attendee1@example.com", "attendee2@example.com");

        List<EventAttendee> attendees = mapper.toEventAttendees(attendeeEmails);

        assertThat(attendees).isNotNull();
        assertThat(attendees).hasSize(2);
        assertThat(attendees.get(0).getEmail()).isEqualTo("attendee1@example.com");
        assertThat(attendees.get(1).getEmail()).isEqualTo("attendee2@example.com");
    }

    @Test
    void testFromEventAttendees() {
        List<EventAttendee> attendees = Arrays.asList(
                new EventAttendee().setEmail("attendee1@example.com"),
                new EventAttendee().setEmail("attendee2@example.com")
        );

        List<String> attendeeEmails = mapper.fromEventAttendees(attendees);

        assertThat(attendeeEmails).isNotNull();
        assertThat(attendeeEmails).hasSize(2);
        assertThat(attendeeEmails).containsExactly("attendee1@example.com", "attendee2@example.com");
    }
}
