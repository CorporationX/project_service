package faang.school.projectservice.exception.stage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class StageException extends RuntimeException{
    private String message;
}
