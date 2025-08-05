package faang.school.projectservice.exception;

import org.slf4j.helpers.MessageFormatter;

public class ResizeException extends RuntimeException {
    public ResizeException(String messagePattern, Object... argArray) {
        super(MessageFormatter.arrayFormat(messagePattern, argArray).getMessage());
    }
}
