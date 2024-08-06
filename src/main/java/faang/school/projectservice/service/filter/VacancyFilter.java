package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.vacancy.filter.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;

import java.util.function.Supplier;
import java.util.stream.Stream;

public interface VacancyFilter {
    boolean isApplicable(VacancyFilterDto filters);

    Stream<Vacancy> apply(Supplier<Stream<Vacancy>> vacancyStream, VacancyFilterDto filters);
}
