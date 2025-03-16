package faang.school.projectservice.filter.vacancy;

import faang.school.projectservice.dto.vacancy.FilterVacancyRequestDto;
import faang.school.projectservice.model.Vacancy;

import java.util.stream.Stream;

public interface VacancyFilter {
    boolean isApplicable(FilterVacancyRequestDto filterDto);

    Stream<Vacancy> apply(Stream<Vacancy> source, FilterVacancyRequestDto filterDto);
}
