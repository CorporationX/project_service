package faang.school.projectservice.exception;

import org.slf4j.helpers.MessageFormatter;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String messagePattern, Object... argArray) {
        super(MessageFormatter.arrayFormat(messagePattern, argArray).getMessage());
    }
}
