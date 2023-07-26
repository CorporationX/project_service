package faang.school.projectservice.service.VacancyFilters;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;

import java.util.List;
import java.util.stream.Stream;

public class VacancyStatusFilter implements VacancyFilter {
    @Override
    public boolean isApplicable(VacancyFilterDto filter) {
        return filter.getVacancyStatus() != null;
    }

    @Override
    public List<Vacancy> apply(Stream<Vacancy> vacancies, VacancyFilterDto filter) {
        return vacancies.filter(vac -> vac.getStatus() == filter.getVacancyStatus()).toList();
    }
}
