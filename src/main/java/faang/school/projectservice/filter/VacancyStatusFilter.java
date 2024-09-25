package faang.school.projectservice.filter;

import faang.school.projectservice.dto.filter.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;

import java.util.stream.Stream;

public class VacancyStatusFilter implements VacancyFilter{

    @Override
    public boolean isApplicable(VacancyFilterDto filters) {
        return filters.getStatusPattern() != null;
    }

    @Override
    public Stream<Vacancy> apply(Stream<Vacancy> vacancies, VacancyFilterDto filters) {
        return vacancies.filter(vacancy -> vacancy.getStatus().equals(filters.getStatusPattern()));
    }

}
