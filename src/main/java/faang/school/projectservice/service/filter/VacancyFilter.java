package faang.school.projectservice.service.filter;

import faang.school.projectservice.model.Vacancy;

import java.util.stream.Stream;

public interface VacancyFilter {
    boolean isApplicable(String position, String name);

    Stream<Vacancy> apply(Stream<Vacancy> vacancyStream, String position, String name);
}
