package faang.school.projectservice.exception.notFoundException.project;

import faang.school.projectservice.exception.notFoundException.EntityNotFoundException;

public class ProjectNotFoundException extends EntityNotFoundException {
    public ProjectNotFoundException(String message) {
        super(message);
    }
}