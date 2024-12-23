package faang.school.projectservice.exception;

import lombok.Getter;

@Getter
public class FileException extends RuntimeException {
    private final ErrorMessage errorMessage;

    public FileException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
    }

    public FileException(ErrorMessage errorMessage, Throwable cause) {
        super(errorMessage.getMessage(), cause);
        this.errorMessage = errorMessage;
    }
}
