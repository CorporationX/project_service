package faang.school.projectservice.service.exception.notFoundException.task;

import faang.school.projectservice.service.exception.notFoundException.EntityNotFoundException;

public class TaskNotFoundException extends EntityNotFoundException {
    public TaskNotFoundException(String message) {
        super(message);
    }
}
