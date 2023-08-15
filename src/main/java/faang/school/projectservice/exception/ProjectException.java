package faang.school.projectservice.exception;

import lombok.Getter;

@Getter
public class ProjectException extends RuntimeException {
    public ProjectException(String message) {
        super(message);
    }
}