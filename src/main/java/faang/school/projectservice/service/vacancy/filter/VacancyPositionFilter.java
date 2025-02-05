package faang.school.projectservice.service.vacancy.filter;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class VacancyPositionFilter implements VacancyFilter {
    @Override
    public boolean isApplicable(VacancyFilterDto filters) {
        return filters != null && filters.getPosition() != null;
    }

    @Override
    public Stream<Vacancy> apply(Stream<Vacancy> vacancies, VacancyFilterDto filters) {
        if (!validateParameters(vacancies, filters) || filters.getPosition() == null) {
            return Stream.empty();
        }

        return vacancies.filter(vacancy -> vacancy.getPosition() != null &&
                vacancy.getPosition() == filters.getPosition());
    }
}
