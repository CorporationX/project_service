package faang.school.projectservice.service.vacancy.filter;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class VacancyNameFilter implements VacancyFilter {
    @Override
    public boolean isApplicable(VacancyFilterDto filters) {
        return filters != null && filters.getName() != null && !filters.getName().isEmpty();
    }

    @Override
    public Stream<Vacancy> apply(Stream<Vacancy> vacancies, VacancyFilterDto filters) {
        if (!validateParameters(vacancies, filters) || filters.getName() == null || filters.getName().isEmpty()) {
            return Stream.empty();
        }

        return vacancies.filter(vacancy -> vacancy.getName() != null &&
                vacancy.getName().toLowerCase().contains(filters.getName().toLowerCase()));
    }
}
