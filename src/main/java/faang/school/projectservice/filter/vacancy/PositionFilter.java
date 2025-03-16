package faang.school.projectservice.filter.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyFilterRequestDto;
import faang.school.projectservice.model.Vacancy;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class PositionFilter implements VacancyFilter {

    @Override
    public boolean isApplicable(VacancyFilterRequestDto filterDto) {
        return filterDto.position() != null;
    }

    @Override
    public Stream<Vacancy> apply(Stream<Vacancy> source, VacancyFilterRequestDto filterDto) {
        return source.filter(request -> request.getPosition() == filterDto.position());
    }
}
