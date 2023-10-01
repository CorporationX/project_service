package faang.school.projectservice.exception.notFoundException.task;

import faang.school.projectservice.exception.notFoundException.EntityNotFoundException;

public class TaskNotFoundException extends EntityNotFoundException {
    public TaskNotFoundException(String message) {
        super(message);
    }
}
