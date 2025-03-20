package faang.school.projectservice.filter.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class VacancyNameFilter implements  VacancyFilter {

    @Override
    public boolean  isApplicable(VacancyFilterDto filter) {
        return filter.namePattern() != null
                && !filter.namePattern().isBlank();

    }

    @Override
    public Stream<Vacancy> apply(Stream<Vacancy> vacancies, VacancyFilterDto filter) {
        if (!isApplicable(filter)) {
            return vacancies;
        }
        return vacancies.filter(vacancy -> vacancy.getName() != null
                && !vacancy.getName().isBlank()
                && vacancy.getName().toLowerCase().contains(filter.namePattern().toLowerCase()));
    }
}
