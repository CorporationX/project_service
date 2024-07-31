package faang.school.projectservice.helper.exeption;

public class StageNotFoundException extends RuntimeException {

    public StageNotFoundException() {
        super("Этап не найден. Для начала нужно создать этап.");
    }
}
