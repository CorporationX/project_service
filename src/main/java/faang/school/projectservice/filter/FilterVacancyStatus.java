package faang.school.projectservice.filter;

import faang.school.projectservice.dto.vacancy.VacancyDtoFilter;
import faang.school.projectservice.model.Vacancy;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class FilterVacancyStatus implements VacancyFilter {
    @Override
    public boolean isApplicable(VacancyDtoFilter filter) {
        return  filter.getStatus() != null;
    }

    @Override
    public Stream<Vacancy> apply(Stream<Vacancy> vacancies, VacancyDtoFilter filters) {
        return vacancies.filter(vacancy -> vacancy.getStatus() == filters.getStatus());
    }
}
