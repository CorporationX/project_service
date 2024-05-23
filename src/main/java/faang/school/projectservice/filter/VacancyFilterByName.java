package faang.school.projectservice.filter;

import faang.school.projectservice.dto.client.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class VacancyFilterByName implements VacancyFilter {

    public boolean isAcceptable(VacancyFilterDto vacancyFilterDto) {
        return vacancyFilterDto.getName() != null;
    }

    public Stream<Vacancy> applyFilter(Stream<Vacancy> vacancies, VacancyFilterDto vacancyFilter) {
        return vacancies.filter(vacancy -> vacancy.getName().contains(vacancyFilter.getName()));
    }
}
