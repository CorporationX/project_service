package faang.school.projectservice.exception;

import lombok.AllArgsConstructor;


public class AccessDeniedException extends RuntimeException{
    public AccessDeniedException(String message){
        super(message);
    }
}
