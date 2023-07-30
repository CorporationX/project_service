package faang.school.projectservice.exception.project;

import lombok.Getter;

@Getter
public class ProjectException extends RuntimeException {
    public ProjectException(String message) {
        super(message);
    }
}
