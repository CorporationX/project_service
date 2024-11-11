package faang.school.projectservice.model.filter;

import faang.school.projectservice.dto.client.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;

import java.util.stream.Stream;

public class VacancyFilterByName implements VacancyFilter {
    @Override
    public boolean isApplicable(VacancyFilterDto vacancyFilterDto) {
        return vacancyFilterDto.getNamePattern() == null;
    }

    @Override
    public Stream<Vacancy> apply(Stream<Vacancy> vacancyStream, VacancyFilterDto vacancyFilterDto) {
        return vacancyStream.filter(vacancy -> vacancy.getName().contains(vacancyFilterDto.getNamePattern()));
    }
}
