package faang.school.projectservice.filter;

import faang.school.projectservice.dto.vacancy.VacancyDtoFilter;
import faang.school.projectservice.model.Vacancy;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class FilterVacancyName implements VacancyFilter {
    @Override
    public boolean isApplicable(VacancyDtoFilter filter) {
        return filter.getName() != null;
    }

    @Override
    public Stream<Vacancy> apply(Stream<Vacancy> vacancies, VacancyDtoFilter filter) {
        return vacancies.filter(vacancy -> vacancy.getName().contains(filter.getName()));
    }
}
