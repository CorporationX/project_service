package faang.school.projectservice.service.vacancy.filter;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;

import java.util.List;

public class VacancyNameFilter implements VacancyFilter {
    @Override
    public boolean isApplicable(VacancyFilterDto filters) {
        return filters.getName() != null;
    }

    @Override
    public void apply(List<Vacancy> vacancies, VacancyFilterDto filters) {
        vacancies.removeIf(vacancy -> !vacancy.getName().contains(filters.getName()));
    }
}
