package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;

import java.util.function.Supplier;
import java.util.stream.Stream;

public interface VacancyFilter {

    boolean isApplicable(VacancyFilterDto filters);

    Stream<Vacancy> apply(Supplier<Stream<Vacancy>> requests, VacancyFilterDto filters);
}
