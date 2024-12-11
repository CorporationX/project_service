package faang.school.projectservice.exception.project;

import java.io.IOException;

public class ImageValidationFailException extends IOException {
    public ImageValidationFailException(String message) {
        super(message);
    }

    public ImageValidationFailException(String message, Throwable cause) {
        super(message, cause);
    }
}
