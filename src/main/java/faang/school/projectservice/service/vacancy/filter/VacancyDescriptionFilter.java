package faang.school.projectservice.service.vacancy.filter;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VacancyDescriptionFilter implements VacancyFilter {
    @Override
    public boolean isApplicable(VacancyFilterDto filters) {
        return filters.getDescription() != null;
    }

    @Override
    public void apply(List<Vacancy> vacancies, VacancyFilterDto filters) {
        vacancies.removeIf(vacancy -> !vacancy.getDescription().contains(filters.getDescription()));
    }
}
