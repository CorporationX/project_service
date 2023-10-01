package faang.school.projectservice.exception.notFoundException.team;

import faang.school.projectservice.exception.notFoundException.EntityNotFoundException;

public class TeamNotFoundException extends EntityNotFoundException {
    public TeamNotFoundException(String message) {
        super(message);
    }
}
