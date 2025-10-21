package faang.school.projectservice.filter;

import faang.school.projectservice.dto.vacancy.SearchVacancyDto;
import faang.school.projectservice.model.Vacancy;

import java.util.stream.Stream;

public interface VacancyFilter {
    boolean isApplicable(SearchVacancyDto searchVacancyDto);

    Stream<Vacancy> apply(Stream<Vacancy> vacancies, SearchVacancyDto searchVacancyDto);
}
