package faang.school.projectservice.filter.vacancy.impl;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.filter.vacancy.Filter;
import faang.school.projectservice.model.Vacancy;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Alexander Bulgakov
 */

@Component
public class VacancyNameFilter implements Filter<VacancyFilterDto, Vacancy> {
    @Override
    public boolean isApplicable(VacancyFilterDto filter) {
        return filter.getName() != null;
    }

    @Override
    public void apply(List<Vacancy> vacancies, VacancyFilterDto filter) {
        vacancies.removeIf(vacancy -> !vacancy.getName().contains(filter.getName()));
    }
}
