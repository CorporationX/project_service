package faang.school.projectservice.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ControllerError {
    private int status;
    private String message;

    public ControllerError(int status) {
        this.status = status;
    }
}