package faang.school.projectservice.exception;

import java.io.IOException;

public class ImageResizeException extends RuntimeException {
    public ImageResizeException(String message, IOException e) {
        super(message);
    }
}
