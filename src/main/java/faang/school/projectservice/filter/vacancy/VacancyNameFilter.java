package faang.school.projectservice.filter.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.vacancy.Vacancy;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class VacancyNameFilter implements VacancyFilter {

    @Override
    public boolean isApplicable(VacancyFilterDto filter) {
        return filter.namePattern() != null;
    }

    @Override
    public Stream<Vacancy> apply(Stream<Vacancy> vacancies, VacancyFilterDto filter) {
        return vacancies.filter(vacancy -> vacancy.getName().contains(filter.namePattern()));
    }
}