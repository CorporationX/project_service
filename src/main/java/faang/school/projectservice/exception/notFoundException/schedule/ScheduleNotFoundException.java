package faang.school.projectservice.exception.notFoundException.schedule;

import faang.school.projectservice.exception.notFoundException.EntityNotFoundException;

public class ScheduleNotFoundException extends EntityNotFoundException {
    public ScheduleNotFoundException(String message) {
        super(message);
    }
}
