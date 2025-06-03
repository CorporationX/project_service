package faang.school.projectservice.exception;

BJS2-77500
hydra-master-stream10
public class VacancyNotFoundException extends RuntimeException {
    public VacancyNotFoundException(Long id) {
        super(String.format("Vacancy with id=%d not found", id));
    }
}
