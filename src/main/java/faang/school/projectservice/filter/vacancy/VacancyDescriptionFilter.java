package faang.school.projectservice.filter.vacancy;

import faang.school.projectservice.dto.Vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class VacancyDescriptionFilter implements VacancyFilter {
    @Override
    public boolean isApplicable(VacancyFilterDto filters) {
        return filters.getDescriptionPattern() != null;
    }

    @Override
    public Stream<Vacancy> apply(Stream<Vacancy> vacancies, VacancyFilterDto filters) {
        return vacancies.filter(vacancy -> vacancy.getDescription().contains(filters.getDescriptionPattern()));
    }
}

