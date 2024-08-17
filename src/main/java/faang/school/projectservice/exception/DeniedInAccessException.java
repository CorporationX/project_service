package faang.school.projectservice.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeniedInAccessException extends IllegalArgumentException {
    public DeniedInAccessException(long userId) {
        super(String.format("Denied in access to user with ID: %d", userId));
        log.error(super.getMessage());
    }
}
