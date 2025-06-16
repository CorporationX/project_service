package faang.school.projectservice.filter.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.Vacancy;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class PositionFilter implements Filter<Vacancy, VacancyFilterDto> {
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