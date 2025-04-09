package faang.school.projectservice.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TaskStatus {
    TODO("TODO"),
    IN_PROGRESS("IN_PROGRESS"),
    REVIEW("REVIEW"),
    TESTING("TESTING"),
    DONE("DONE"),
    CANCELLED("CANCELLED");

    private final String value;
}