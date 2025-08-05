package faang.school.projectservice.exception;

import org.slf4j.helpers.MessageFormatter;

public class BlankFieldException extends RuntimeException {
    public BlankFieldException(String messagePattern, Object... argArray) {
        super(MessageFormatter.arrayFormat(messagePattern, argArray).getMessage());
    }
}
