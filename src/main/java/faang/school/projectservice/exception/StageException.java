package faang.school.projectservice.exception;

import lombok.Getter;


@Getter
public class StageException extends RuntimeException {
    public StageException(String message) {
        super(message);
    }
}
