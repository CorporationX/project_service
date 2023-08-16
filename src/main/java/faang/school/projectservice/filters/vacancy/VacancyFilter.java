package faang.school.projectservice.filters.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;

import java.util.stream.Stream;

public interface VacancyFilter {
    boolean isApplicable(VacancyFilterDto vacancyFilterDto);

    Stream<Vacancy> apply(Stream<Vacancy> vacancies, VacancyFilterDto filterDto);
}
