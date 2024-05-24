package faang.school.projectservice.service.vacancy.filters;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class VacancyFilterService {
    private final List<VacancyFilter> filters;

    public Stream<Vacancy> applyFilter(Stream<Vacancy> vacancies, VacancyFilterDto vacancyFilterDto) {
        return filters.stream()
                .filter(filter -> filter.isAcceptable(vacancyFilterDto))
                .flatMap(filter -> filter.applyFilter(vacancies, vacancyFilterDto));
    }
}
