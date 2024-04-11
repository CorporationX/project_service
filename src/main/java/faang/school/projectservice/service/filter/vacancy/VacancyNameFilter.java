package faang.school.projectservice.service.filter.vacancy;

import faang.school.projectservice.dto.filter.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;

import java.util.List;
import java.util.stream.Stream;

public class VacancyNameFilter implements VacancyFilter {
    @Override
    public boolean isApplicable(VacancyFilterDto filters) {
        return filters.getNamePattern() != null;
    }

    @Override
    public List<Vacancy> apply(Stream<Vacancy> vacancyStream, VacancyFilterDto filters) {
        return vacancyStream
                .filter(vacancy -> vacancy.getName().equals(filters.getNamePattern()))
                .toList();
    }
}
