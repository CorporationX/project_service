package faang.school.projectservice.service.VacancyFilters;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;

import java.util.List;
import java.util.stream.Stream;

public interface VacancyFilter {

    boolean isApplicable(VacancyFilterDto filter);

    List<Vacancy> apply(Stream<Vacancy> vacancies, VacancyFilterDto filter);
}
