package faang.school.projectservice.filter;

import faang.school.projectservice.model.Vacancy;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class VacancyDescriptionFilter implements VacancyFilter {
    @Override
    public boolean isApplicable(VacancyFilterDto filter) {
        return filter.getDescriptionPattern() != null;
    }

    @Override
    public Stream<Vacancy> apply(VacancyFilterDto filter, Stream<Vacancy> vacancies) {
        return vacancies.filter(vacancy -> vacancy.getDescription().contains(filter.getDescriptionPattern()));
    }
}
