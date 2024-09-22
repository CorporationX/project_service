package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.filter.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;

import java.util.stream.Stream;

public class VacancyNameFilter implements VacancyFilter{

    @Override
    public boolean isApplicable(VacancyFilterDto filters) {
        return filters.getNamePattern() != null;
    }

    @Override
    public Stream<Vacancy> apply(Stream<Vacancy> vacancies, VacancyFilterDto filters) {
        return vacancies.filter(vacancy -> vacancy.getName().contains(filters.getNamePattern()));
    }

}
