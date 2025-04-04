package faang.school.projectservice.exception;

public class CoverMaxSizeException extends CustomException {

    public CoverMaxSizeException(ExceptionMessage message, long size) {
        super(message, size);
    }
}
