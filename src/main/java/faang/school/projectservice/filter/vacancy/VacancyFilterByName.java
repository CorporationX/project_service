package faang.school.projectservice.filter.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class VacancyFilterByName implements VacancyFilter {

    @Override
    public boolean isApplicable(VacancyFilterDto filterDto) {
        return StringUtils.isNotBlank(filterDto.getNamePattern());
    }

    @Override
    public Stream<Vacancy> apply(Stream<Vacancy> itemStream, VacancyFilterDto filterDto) {
        return itemStream.filter(vacancy -> vacancy.getName().equalsIgnoreCase(filterDto.getNamePattern()));
    }
}
