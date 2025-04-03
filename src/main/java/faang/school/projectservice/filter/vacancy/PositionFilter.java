package faang.school.projectservice.filter.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;

import java.util.stream.Stream;

public class PositionFilter implements VacancyFilter{
    @Override
    public boolean isApplicable(VacancyFilterDto filter){
        return filter.getPosition() != null && !filter.getPosition().isBlank();
    }

    @Override
    public Stream<Vacancy> apply(Stream<Vacancy> elements, VacancyFilterDto filter) {
        String searchPosition = filter.getPosition();
        return elements.filter(vacancy -> vacancy.getPosition().toString().equals(searchPosition));
    }
}
