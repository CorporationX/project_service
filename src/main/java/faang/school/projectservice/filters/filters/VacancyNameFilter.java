package faang.school.projectservice.filters.filters;

import faang.school.projectservice.dto.client.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;
@Component
public class VacancyNameFilter implements VacancyFilterable {
    @Override
    public boolean isValid(VacancyFilterDto dto) {
        return dto.getName()!=null && !dto.getName().isEmpty();
    }

    @Override
    public Stream<Vacancy> apply(Stream<Vacancy> stream, VacancyFilterDto dto) {
        return stream.filter(el->el.getName().equals(dto.getName()));
    }
}
