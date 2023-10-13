package faang.school.projectservice.service.exception.notFoundException.team;

import faang.school.projectservice.service.exception.notFoundException.EntityNotFoundException;

public class TeamNotFoundException extends EntityNotFoundException {
    public TeamNotFoundException(String message) {
        super(message);
    }
}
