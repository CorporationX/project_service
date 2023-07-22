package faang.school.projectservice.model;

public enum ProjectStatus {
    CREATED("Project Created"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    ON_HOLD("On Hold"),
    CANCELLED("Cancelled");
    private final String statusName;

    ProjectStatus(String statusName) {
        this.statusName = statusName;
    }

    @Override
    public String toString() {
        return statusName;

    }
}