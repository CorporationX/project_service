package faang.school.projectservice.service.VacancyFilters;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public class VacancyNameFilter implements VacancyFilter {
    @Override
    public boolean isApplicable(VacancyFilterDto filter) {
        return filter.getName() != null;
    }

    @Override
    public List<Vacancy> apply(Stream<Vacancy> vacancies, VacancyFilterDto filter) {
        return vacancies.filter(vac -> vac.getName().equals(filter.getName())).toList();
    }
}
