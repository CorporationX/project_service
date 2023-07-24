package faang.school.projectservice.exception.vacancy;

public class VacancyValidateException extends RuntimeException {
    public VacancyValidateException(String errorMessage) {
        super(errorMessage);
    }
}
