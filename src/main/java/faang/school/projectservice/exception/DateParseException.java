package faang.school.projectservice.exception;

public class DateParseException extends CustomException {

    public DateParseException(String date) {
        super(ExceptionMessage.DATE_PARSE, date);
    }
}
