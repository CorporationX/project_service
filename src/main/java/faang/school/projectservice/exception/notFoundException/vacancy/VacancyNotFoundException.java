package faang.school.projectservice.exception.notFoundException.vacancy;

import faang.school.projectservice.exception.notFoundException.EntityNotFoundException;

public class VacancyNotFoundException extends EntityNotFoundException {
    public VacancyNotFoundException(String message) {
        super(message);
    }
}