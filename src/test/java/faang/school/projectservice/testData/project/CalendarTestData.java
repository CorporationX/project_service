package faang.school.projectservice.testData.project;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.AclRule;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import faang.school.projectservice.dto.project.calendar.AclDto;
import faang.school.projectservice.dto.project.calendar.CalendarDto;
import faang.school.projectservice.dto.project.calendar.EventDto;
import faang.school.projectservice.dto.project.calendar.ScopeDto;
import faang.school.projectservice.model.aclRole.AclRole;
import faang.school.projectservice.model.aclRole.AclScopeType;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

@Getter
public class CalendarTestData {
    private final EventDto eventDto;
    private final Event event;
    private final CalendarDto calendarDto;
    private final Calendar calendar;
    private final AclDto aclDto;
    private final AclRule acl;

    public CalendarTestData() {
        eventDto = createEventDto();
        event = createEvent();
        calendarDto = createCalendarDto();
        calendar = createCalendar();
        aclDto = createAclDto();
        acl = createAcl();
    }

    private AclRule createAcl() {
        return new AclRule()
                .setId("id")
                .setRole(AclRole.READER.getRole())
                .setScope(new AclRule.Scope().setType(AclScopeType.DEFAULT.getType()));
    }

    private Calendar createCalendar() {
        return new Calendar()
                .setId("id")
                .setDescription("Test description")
                .setLocation("Test location")
                .setSummary("Test summary");
    }

    private Event createEvent() {
        return new Event()
                .setId("id")
                .setDescription("Test description")
                .setSummary("Test summary")
                .setStart(eventDateMapping(eventDto.getStartTime()))
                .setEnd(eventDateMapping(eventDto.getEndTime()));
    }

    private AclDto createAclDto() {
        return AclDto.builder()
                .id("id")
                .role(AclRole.READER)
                .scope(ScopeDto.builder().type(AclScopeType.DEFAULT).build())
                .build();
    }

    private CalendarDto createCalendarDto() {
        return CalendarDto.builder()
                .id("id")
                .description("Test description")
                .location("Test location")
                .summary("Test summary")
                .build();
    }

    private EventDto createEventDto() {
        return EventDto.builder()
                .id("id")
                .description("Test description")
                .location("Test location")
                .summary("Test summary")
                .startTime(LocalDateTime.now().plus(1, ChronoUnit.DAYS))
                .endTime(LocalDateTime.now().plus(2, ChronoUnit.DAYS))
                .build();
    }

    private EventDateTime eventDateMapping(LocalDateTime date) {
        DateTime dateTime = new DateTime(ZonedDateTime.of(date, ZoneId.systemDefault()).toInstant().toEpochMilli());
        return new EventDateTime().setDateTime(dateTime);
    }
}
