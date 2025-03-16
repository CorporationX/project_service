package faang.school.projectservice.filter.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyFilterRequestDto;
import faang.school.projectservice.model.Vacancy;

import java.util.stream.Stream;

public interface VacancyFilter {
    boolean isApplicable(VacancyFilterRequestDto filterDto);

    Stream<Vacancy> apply(Stream<Vacancy> source, VacancyFilterRequestDto filterDto);
}
