package faang.school.projectservice.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageError {
    NAME_IS_EMPTY("Name is empty");

    private final String message;
}
