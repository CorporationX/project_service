package faang.school.projectservice.service.exception.notFoundException.project;

import faang.school.projectservice.service.exception.notFoundException.EntityNotFoundException;

public class ProjectNotFoundException extends EntityNotFoundException {
    public ProjectNotFoundException(String message) {
        super(message);
    }
}