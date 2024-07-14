package faang.school.projectservice.filter;

import faang.school.projectservice.dto.filter.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;

import java.util.stream.Stream;

public interface Filter<F, D> {
    boolean isApplicable(F filters);

    Stream<D> apply(Stream<D> vacancies, F filters);
}
