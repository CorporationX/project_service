package faang.school.projectservice.validator;

import faang.school.projectservice.dto.EventDto;
import faang.school.projectservice.exception.DataValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CalendarServiceImplValidator {
    public void validateUpdate(EventDto eventDto) {
        if (eventDto.getCalendarEventId() == null) {
            throw new DataValidationException("cant update calendar event: event has not been in calendar id = " + eventDto.getId());
        }
    }
}
