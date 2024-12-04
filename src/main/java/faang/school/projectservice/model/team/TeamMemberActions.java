package faang.school.projectservice.model.team;

import lombok.Getter;

@Getter
public enum TeamMemberActions {
    ADD_MEMBER("adding a team member"),
    UPDATE_MEMBER("updating a team member"),
    REMOVE_MEMBER("removing a team member");

    private final String description;

    TeamMemberActions(String description) {
        this.description = description;
    }
}
