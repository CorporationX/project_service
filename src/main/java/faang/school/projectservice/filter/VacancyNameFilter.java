package faang.school.projectservice.filter;

import faang.school.projectservice.dto.vacancy.SearchVacancyDto;
import faang.school.projectservice.model.Vacancy;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class VacancyNameFilter implements VacancyFilter {

    @Override
    public boolean isApplicable(SearchVacancyDto searchVacancyDto) {
        return searchVacancyDto.vacancyName() != null && !searchVacancyDto.vacancyName().isBlank();
    }

    @Override
    public Stream<Vacancy> apply(Stream<Vacancy> vacancies, SearchVacancyDto searchVacancyDto) {
        return vacancies
                .filter(vacancy ->
                        vacancy.getName().toLowerCase()
                                .contains(searchVacancyDto.vacancyName().toLowerCase()));
    }
}
