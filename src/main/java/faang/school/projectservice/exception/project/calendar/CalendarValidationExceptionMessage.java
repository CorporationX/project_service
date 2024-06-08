package faang.school.projectservice.exception.project.calendar;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CalendarValidationExceptionMessage {
    END_TIME_IS_BEFORE_START_EXCEPTION("Event end time must be after start time.");

    private final String message;
}
