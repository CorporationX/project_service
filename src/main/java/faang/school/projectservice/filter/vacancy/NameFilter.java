package faang.school.projectservice.filter.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.Vacancy;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class NameFilter implements Filter<Vacancy, VacancyFilterDto> {
    @Override
    public boolean isApplicable(VacancyFilterDto filter) {
        return filter.getName() != null && !filter.getName().isBlank();
    }

    @Override
    public Stream<Vacancy> apply(Stream<Vacancy> elements, VacancyFilterDto filter) {
        String searchName = filter.getName().toLowerCase();
        return elements.filter(vacancy -> vacancy.getName().toLowerCase().contains(searchName));
    }
}