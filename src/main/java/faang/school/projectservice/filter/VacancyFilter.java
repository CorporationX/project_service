package faang.school.projectservice.filter;

import faang.school.projectservice.model.Vacancy;

import java.util.stream.Stream;

public interface VacancyFilter {
    boolean isApplicable(VacancyFilterDto vacancyFilterDto);

    Stream<Vacancy> apply(VacancyFilterDto vacancyFilterDto, Stream<Vacancy> vacancies);
}
