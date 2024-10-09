package faang.school.projectservice.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Violation {

    private final String fieldName;
    private final String message;
}
