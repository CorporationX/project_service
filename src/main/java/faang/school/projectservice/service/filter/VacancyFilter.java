package faang.school.projectservice.service.filter;

import faang.school.projectservice.model.Vacancy;

public interface VacancyFilter {
    boolean test(Vacancy vacancy);
}
