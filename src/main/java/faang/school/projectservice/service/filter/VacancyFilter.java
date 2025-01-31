package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;
import java.util.stream.Stream;

public interface VacancyFilter {
    boolean isApplicable(VacancyFilterDto filter);

    Stream<Vacancy> apply(VacancyFilterDto filter, Stream<Vacancy> subtotal);
}
