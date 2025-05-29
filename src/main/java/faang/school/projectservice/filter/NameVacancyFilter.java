package faang.school.projectservice.filter;

import faang.school.projectservice.model.Vacancy;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class NameVacancyFilter implements VacancyFilter {

    @Override
    public boolean isApplicable(String position, String name) {
        return name != null;
    }

    @Override
    public Stream<Vacancy> apply(Stream<Vacancy> vacancyStream, String position, String name) {
        return vacancyStream.filter(vacancy -> vacancy.getName().equalsIgnoreCase(name));
    }
}