package faang.school.projectservice.filters.filters;

import faang.school.projectservice.dto.client.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;

import java.util.stream.Stream;

public interface VacancyFilterable {
    boolean isValid(VacancyFilterDto dto);
    Stream<Vacancy> apply(Stream<Vacancy> stream, VacancyFilterDto dto);
}
