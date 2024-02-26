package faang.school.projectservice.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ResponseError {
    private int status;
    private String message;

    public ResponseError(int status) {
        this.status = status;
    }
}