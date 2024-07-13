package faang.school.projectservice.service.vacancy.filter;

import faang.school.projectservice.dto.filter.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;

import java.util.stream.Stream;

public interface VacancyFilter {
    boolean isApplicable(VacancyFilterDto filters);

    Stream<Vacancy> apply(Stream<Vacancy> vacancies, VacancyFilterDto filters);
}
