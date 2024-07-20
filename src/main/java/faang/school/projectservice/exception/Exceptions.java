package faang.school.projectservice.exception;

import org.springframework.stereotype.Component;

@Component
public class Exceptions {
    public void validateInputValuesIsNull() {
        throw new DataValidationException("Входящий аргумент не должен быть пустым");
    }

    public void validateInputValuesNotValidate() {
        throw new DataValidationException("Входящие данные не прошли проверку");
    }
}
