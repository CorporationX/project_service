package faang.school.projectservice.exception;

public class DateParseException extends CustomException {

    public DateParseException(ExceptionMessage message, String date) {
        super(message, date);
    }
}
