package faang.school.projectservice.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

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

    public static List<String> getAllStatusNames() {
        List<String> statusNames = new ArrayList<>();

        for (ProjectStatus status : ProjectStatus.values()) {
            statusNames.add(status.getStatusName());
        }

        return statusNames;
    }
}