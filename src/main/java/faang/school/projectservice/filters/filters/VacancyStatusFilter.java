package faang.school.projectservice.filters.filters;

import faang.school.projectservice.dto.client.VacancyDto;
import faang.school.projectservice.dto.client.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;


@Component
public class VacancyStatusFilter implements Filterable {


    @Override
    public boolean isValid(VacancyFilterDto dto) {
        return dto.getStatus()!=null;
    }

    @Override
    public Stream<Vacancy> apply(Stream<Vacancy> stream, VacancyFilterDto dto) {
        return stream.filter(el->el.getStatus().equals(dto.getStatus()));
    }
}
