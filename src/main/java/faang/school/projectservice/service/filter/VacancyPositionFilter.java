package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;

import java.util.stream.Stream;

public class VacancyPositionFilter implements VacancyFilter {
    @Override
    public boolean isApplicable(VacancyFilterDto filter) {
        return filter.position() != null;
    }

    @Override
    public Stream<Vacancy> apply(VacancyFilterDto filter, Stream<Vacancy> vacancies) {
        return vacancies
                .filter(vacancy -> vacancy.getPosition().equals(filter.position()));
    }
}
