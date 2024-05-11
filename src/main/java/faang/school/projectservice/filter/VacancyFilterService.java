package faang.school.projectservice.filter;

import faang.school.projectservice.dto.client.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class VacancyFilterService {
    private final List<VacancyFilter> vacancyFilters;

    public Stream<Vacancy> applyFilter(Stream<Vacancy> vacancies, VacancyFilterDto vacancyFilterDto) {
        if (vacancyFilterDto != null) {
            for (VacancyFilter vacancyFilter : vacancyFilters) {
                if (vacancyFilter.isAcceptable(vacancyFilterDto)) {
                    vacancies = vacancyFilter.applyFilter(vacancies, vacancyFilterDto);
                }
            }
        }
        return vacancies;
    }
}
