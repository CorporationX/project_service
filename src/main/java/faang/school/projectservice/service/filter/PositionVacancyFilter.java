package faang.school.projectservice.service.filter;

import faang.school.projectservice.model.Vacancy;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class PositionVacancyFilter implements VacancyFilter {

    @Override
    public boolean isApplicable(String position, String name) {
        return position != null;
    }

    @Override
    public Stream<Vacancy> apply(Stream<Vacancy> vacancyStream, String position, String name) {
        return vacancyStream
                .filter(vacancy ->
                        vacancy.getPosition().name().equalsIgnoreCase(position));
    }
}
