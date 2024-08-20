package faang.school.projectservice.filter;

import faang.school.projectservice.dto.client.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;

import java.util.stream.Stream;

public interface VacancyFilter {
    boolean isValid(VacancyFilterDto dto);
    Stream<Vacancy> apply(Stream<Vacancy> stream, VacancyFilterDto dto);
}
