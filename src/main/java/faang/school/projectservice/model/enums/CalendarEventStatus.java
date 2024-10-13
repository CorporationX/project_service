package faang.school.projectservice.model.enums;

public enum CalendarEventStatus {
    TENTATIVE,
    CONFIRMED,
    CANCELLED;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
