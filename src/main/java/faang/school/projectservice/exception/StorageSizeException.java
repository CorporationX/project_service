package faang.school.projectservice.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public class StorageSizeException extends RuntimeException{
    //private Long freeSpace;
    public StorageSizeException(String message){
        super(message);
        //this.freeSpace = freeSpace;
    }
}
