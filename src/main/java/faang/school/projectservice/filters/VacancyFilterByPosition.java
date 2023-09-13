package faang.school.projectservice.filters;

import faang.school.projectservice.dto.project.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;

import java.util.stream.Stream;

public class VacancyFilterByPosition implements VacancyFilter{

    @Override
    public boolean isApplicable(VacancyFilterDto vacancyFilterDto) {
        return vacancyFilterDto.getPosition() != null;
    }

    @Override
    public Stream<Vacancy> apply(Stream<Vacancy> vacancyStream, VacancyFilterDto vacancyFilterDto) {
        return vacancyStream
                .filter(vacancy -> vacancy.getPosition().contains(vacancyFilterDto.getPosition()));
    }
}
