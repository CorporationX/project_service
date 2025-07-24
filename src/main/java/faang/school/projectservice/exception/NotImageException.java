package faang.school.projectservice.exception;

import org.slf4j.helpers.MessageFormatter;

public class NotImageException extends RuntimeException {
    public NotImageException(String messagePattern, Object... argArray) {
        super(MessageFormatter.arrayFormat(messagePattern, argArray).getMessage());
    }
}
