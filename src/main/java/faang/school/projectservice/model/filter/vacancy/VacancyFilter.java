package faang.school.projectservice.model.filter.vacancy;

import faang.school.projectservice.model.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.entity.vacancy.Vacancy;

import java.util.stream.Stream;

public interface VacancyFilter {

    boolean isApplicable(VacancyFilterDto filter);

    Stream<Vacancy> apply(Stream<Vacancy> vacancies ,VacancyFilterDto filter);
}