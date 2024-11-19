package faang.school.projectservice.filter.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class VacancyFilterById implements VacancyFilter {
    @Override
    public boolean isApplicable(VacancyFilterDto filterDto) {
        return filterDto.getId() != null && filterDto.getId() > 0;
    }

    @Override
    public Stream<Vacancy> apply(Stream<Vacancy> itemStream, VacancyFilterDto filterDto) {
        return itemStream.filter(item -> Objects.equals(item.getId(), filterDto.getId()));
    }
}
