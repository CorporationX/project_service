package faang.school.projectservice.exception;

public class VacancyNotFoundException extends RuntimeException {
    public VacancyNotFoundException(Long id) {
        super(String.format("Vacancy with id=%d not found", id));
    }
}
