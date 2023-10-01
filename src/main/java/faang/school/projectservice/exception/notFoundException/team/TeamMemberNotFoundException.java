package faang.school.projectservice.exception.notFoundException.team;

import faang.school.projectservice.exception.notFoundException.EntityNotFoundException;

public class TeamMemberNotFoundException extends EntityNotFoundException {
    public TeamMemberNotFoundException(String message) {
        super(message);
    }
}
