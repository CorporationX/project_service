package faang.school.projectservice.service.VacancyFilters;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class VacancyStatusFilter implements VacancyFilter {
    @Override
    public boolean isApplicable(VacancyFilterDto filter) {
        return filter.getVacancyStatus() != null;
    }

    @Override
    public Stream<Vacancy> apply(Stream<Vacancy> vacancies, VacancyFilterDto filter) {
        return vacancies.filter(vac -> vac.getStatus() == filter.getVacancyStatus());
    }
}
