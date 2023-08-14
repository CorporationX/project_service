package faang.school.projectservice.exception.vacancy;

public class VacancyCRUDException extends RuntimeException{
    public VacancyCRUDException(String errorMessage) {
        super(errorMessage);
    }
}
