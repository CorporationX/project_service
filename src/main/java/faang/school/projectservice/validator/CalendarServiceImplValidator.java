package faang.school.projectservice.validator;

import faang.school.projectservice.dto.EventDto;
import faang.school.projectservice.exception.DataValidationException;
import org.springframework.stereotype.Component;

@Component
public class CalendarServiceImplValidator {
    public void validateUpdate(EventDto eventDto) {
        if (eventDto.getCalendarEventId() == null) {
            throw new DataValidationException("cant update calendar event: event has not been in calendar");
        }
    }
}
