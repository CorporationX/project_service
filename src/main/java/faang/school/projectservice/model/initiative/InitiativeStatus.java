package faang.school.projectservice.model.initiative;

public enum InitiativeStatus {

    OPEN,
    CLOSED,
    ACCEPTED,
    IN_PROGRESS,
    DONE;

    public static boolean isActiveInitiative(Initiative initiative) {
        return !initiative.getStatus().equals(CLOSED) && !initiative.getStatus().equals(DONE);
    }
}
