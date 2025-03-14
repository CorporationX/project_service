package faang.school.projectservice.filter.vacancy;

import faang.school.projectservice.dto.vacancy.SearchVacancyDto;
import faang.school.projectservice.model.Vacancy;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class VacancyNameFilter implements VacancyFilter {
    @Override
    public boolean isApplicable(SearchVacancyDto searchVacancyDto) {
        return searchVacancyDto.getName() != null;
    }

    @Override
    public Stream<Vacancy> apply(Stream<Vacancy> vacancies, SearchVacancyDto searchVacancyDto) {
        return vacancies.filter(vacancy -> searchVacancyDto.getName().equalsIgnoreCase(vacancy.getName()));
    }
}
