package faang.school.projectservice.filter;

import faang.school.projectservice.dto.vacancy.VacancyDtoFilter;
import faang.school.projectservice.model.Vacancy;

import java.util.stream.Stream;

public interface VacancyFilter {

    boolean isApplicable(VacancyDtoFilter vacancyDtoFilter);
    Stream<Vacancy> apply(Stream<Vacancy> vacancyStream, VacancyDtoFilter vacancyDtoFilter);
}
