package faang.school.projectservice.model;

public enum MomentType {
    COMPLETED("All subprojects completed");

    private final String description;

    MomentType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
