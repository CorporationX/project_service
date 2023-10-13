package faang.school.projectservice.service.exception.notFoundException.team;

import faang.school.projectservice.service.exception.notFoundException.EntityNotFoundException;

public class TeamMemberNotFoundException extends EntityNotFoundException {
    public TeamMemberNotFoundException(String message) {
        super(message);
    }
}
