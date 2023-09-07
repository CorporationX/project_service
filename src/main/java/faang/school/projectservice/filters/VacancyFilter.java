package faang.school.projectservice.filters;

import faang.school.projectservice.dto.project.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;

import java.util.stream.Stream;

public interface VacancyFilter {

    boolean isApplicable(VacancyFilterDto vacancyFilterDto);

    Stream<Vacancy> apply(Stream<Vacancy> vacancyStream, VacancyFilterDto vacancyFilterDto);
}
