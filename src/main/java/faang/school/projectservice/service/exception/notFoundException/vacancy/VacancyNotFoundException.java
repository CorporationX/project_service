package faang.school.projectservice.service.exception.notFoundException.vacancy;

import faang.school.projectservice.service.exception.notFoundException.EntityNotFoundException;

public class VacancyNotFoundException extends EntityNotFoundException {
    public VacancyNotFoundException(String message) {
        super(message);
    }
}