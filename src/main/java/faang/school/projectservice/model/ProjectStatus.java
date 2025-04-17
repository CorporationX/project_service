package faang.school.projectservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProjectStatus {
    CREATED("Created"),
    IN_PROGRESS("In progress"),
    COMPLETED("Completed"),
    ON_HOLD("On hold"),
    CANCELLED("Cancelled");

    private String name;
}