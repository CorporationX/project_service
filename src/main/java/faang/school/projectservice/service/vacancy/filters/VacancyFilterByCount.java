package faang.school.projectservice.service.vacancy.filters;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;

import java.util.stream.Stream;

public class VacancyFilterByCount implements VacancyFilter {

    @Override
    public boolean isAcceptable(VacancyFilterDto vacancyFilterDto) {
        if (vacancyFilterDto != null) {
            return true;
        } else return vacancyFilterDto.getCount() > 0;
    }

    @Override
    public Stream<Vacancy> applyFilter(Stream<Vacancy> vacancies, VacancyFilterDto vacancyFilterDto) {
        return vacancies.filter(vacancy -> vacancy.getCount() > vacancyFilterDto.getCount());
    }
}
