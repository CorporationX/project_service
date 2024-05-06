package faang.school.projectservice.service.filter.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;

import java.util.List;
import java.util.stream.Stream;

public interface VacancyFilter {
    public boolean isApplicable(VacancyFilterDto vacancyFilterDto);
    public Stream<Vacancy> apply(Stream<Vacancy> vacancies, VacancyFilterDto vacancyFilterDto);
}
