package faang.school.projectservice.filter.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;

import java.util.stream.Stream;

public interface VacancyFilter {

    boolean isApplicable(VacancyFilterDto filterDto);

    Stream<Vacancy> apply(Stream<Vacancy> itemStream, VacancyFilterDto filterDto);
}