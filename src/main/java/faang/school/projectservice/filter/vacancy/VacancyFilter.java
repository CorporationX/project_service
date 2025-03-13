package faang.school.projectservice.filter.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;

import java.util.stream.Stream;

public interface VacancyFilter {
    public boolean isApplicable(VacancyFilterDto filter);

    public Stream<Vacancy> apply(Stream<Vacancy> vacancies, VacancyFilterDto filter);
}
