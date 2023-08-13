package faang.school.projectservice.model;

import lombok.Getter;

@Getter
public enum ProjectStatus {
    CREATED("CREATED"),
    IN_PROGRESS("IN PROGRESS"),
    COMPLETED("COMPLETED"),
    ON_HOLD("ON HOLD"),
    CANCELLED("CANCELLED");
    private final String statusName;

    ProjectStatus(String statusName) {
        this.statusName = statusName;
    }

    @Override
    public String toString() {
        return statusName;
    }
}