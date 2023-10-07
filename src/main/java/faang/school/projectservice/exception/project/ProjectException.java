package faang.school.projectservice.exception.project;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ProjectException extends RuntimeException{
    private String message;
}
