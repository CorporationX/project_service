package faang.school.projectservice.exception;

import org.slf4j.helpers.MessageFormatter;

public class FileException extends RuntimeException {
    public FileException(String messagePattern, Object... argArray) {
        super(MessageFormatter.arrayFormat(messagePattern, argArray).getMessage());
    }
}
