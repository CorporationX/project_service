package faang.school.projectservice.service.vacancy.filter;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;

import java.util.List;

public class VacancyStatusFilter implements VacancyFilter {
    @Override
    public boolean isApplicable(VacancyFilterDto filters) {
        return filters.getStatus() != null;
    }

    @Override
    public void apply(List<Vacancy> vacancies, VacancyFilterDto filters) {
        vacancies.removeIf(vacancy -> !vacancy.getStatus().equals(filters.getStatus()));
    }
}