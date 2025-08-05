package faang.school.projectservice.exception;

import org.slf4j.helpers.MessageFormatter;

public class ResourceNotReceivedException extends RuntimeException {
    public ResourceNotReceivedException(String messagePattern, Object... argArray) {
        super(MessageFormatter.arrayFormat(messagePattern, argArray).getMessage());
    }
}
