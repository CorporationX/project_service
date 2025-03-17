package faang.school.projectservice.exception;

import org.apache.http.MessageConstraintException;
import org.apache.logging.log4j.message.StringFormattedMessage;

public class DataValidationException extends RuntimeException  {

    public DataValidationException(String message) {
        super(message);
    }

    public DataValidationException(String message, Object... args) {
        super(String.format(message, args));
    }
}
