package faang.school.projectservice.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    private LocalDateTime localDateTime;

    public ErrorResponse(String message){
        this.message = message;
    }
}
