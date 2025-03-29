package faang.school.projectservice.filter.vacancy;

import faang.school.projectservice.dto.vacancy.FilterVacancyRequestDto;
import faang.school.projectservice.model.Vacancy;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class PositionFilter implements VacancyFilter {

    @Override
    public boolean isApplicable(FilterVacancyRequestDto filterDto) {
        return filterDto.position() != null;
    }

    @Override
    public Stream<Vacancy> apply(Stream<Vacancy> source, FilterVacancyRequestDto filterDto) {
        return source.filter(request -> request.getPosition() == filterDto.position());
    }
}
