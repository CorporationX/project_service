package faang.school.projectservice.filter.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.Vacancy;

import java.util.stream.Stream;

import org.springframework.stereotype.Component;

@Component
public class PositionFilter implements Filter<VacancyFilterDto, Vacancy> {

    @Override
    public boolean isApplicable(VacancyFilterDto vacancyFilterDto) {
        return vacancyFilterDto.getPosition() != null;
    }

    @Override
    public Stream<Vacancy> applyFilter(Stream<Vacancy> vacancies, VacancyFilterDto vacancyFilterDto) {
        return vacancies.filter(vacancy -> vacancy.getName().equalsIgnoreCase(vacancyFilterDto.getPosition()));
    }
}
