package faang.school.projectservice.dto.google.calendar;

public enum CalendarEventStatus {
    TENTATIVE,
    CONFIRMED,
    CANCELLED;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
