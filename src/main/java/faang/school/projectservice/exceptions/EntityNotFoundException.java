package faang.school.projectservice.exceptions;

public class EntityNotFoundException extends RuntimeException{
    public EntityNotFoundException(String msg){
        super(msg);
    }
}