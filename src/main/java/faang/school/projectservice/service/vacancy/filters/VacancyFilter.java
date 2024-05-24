package faang.school.projectservice.service.vacancy.filters;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;

import java.util.stream.Stream;

public interface VacancyFilter {

    boolean isAcceptable(VacancyFilterDto vacancyFilterDto);

    Stream<Vacancy> applyFilter(Stream<Vacancy> vacancies, VacancyFilterDto vacancyFilterDto);

}