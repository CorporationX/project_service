package faang.school.projectservice.exception;

import org.slf4j.helpers.MessageFormatter;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String messagePattern, Object... argArray) {
        super(MessageFormatter.arrayFormat(messagePattern, argArray).getMessage());
    }
}
