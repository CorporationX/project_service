package faang.school.projectservice.helper.exeption;

public class NotNullProvidedException extends RuntimeException{

    public NotNullProvidedException(){
        super("Передаваемое поле не может быть пустым!");
    }
}
