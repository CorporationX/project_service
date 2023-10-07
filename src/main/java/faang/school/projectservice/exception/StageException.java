package faang.school.projectservice.exception;

import lombok.Data;

@Data
public class StageException extends RuntimeException{
    public StageException(String message){
        super(message);
    }
}
