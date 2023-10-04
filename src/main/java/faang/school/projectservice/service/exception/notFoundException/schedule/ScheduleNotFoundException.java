package faang.school.projectservice.service.exception.notFoundException.schedule;

import faang.school.projectservice.service.exception.notFoundException.EntityNotFoundException;

public class ScheduleNotFoundException extends EntityNotFoundException {
    public ScheduleNotFoundException(String message) {
        super(message);
    }
}
