package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;

import java.util.stream.Stream;

public class VacancyNameFilter implements VacancyFilter {
    @Override
    public boolean isApplicable(VacancyFilterDto filter) {
        return filter.nameContains() != null;
    }

    @Override
    public Stream<Vacancy> apply(VacancyFilterDto filter, Stream<Vacancy> vacancies) {
        return vacancies
                .filter(vacancy -> vacancy.getName().contains(filter.nameContains()));
    }
}
