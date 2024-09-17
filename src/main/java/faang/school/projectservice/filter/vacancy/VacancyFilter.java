package faang.school.projectservice.filter.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.vacancy.Vacancy;

import java.util.stream.Stream;

public interface VacancyFilter {

    boolean isApplicable(VacancyFilterDto filter);

    Stream<Vacancy> apply(Stream<Vacancy> vacancies ,VacancyFilterDto filter);
}