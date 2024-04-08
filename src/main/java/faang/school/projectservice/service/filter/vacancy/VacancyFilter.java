package faang.school.projectservice.service.filter.vacancy;

import faang.school.projectservice.dto.filter.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public interface VacancyFilter {
    public boolean isApplicable(VacancyFilterDto filters);

    public List<Vacancy> apply(Stream<Vacancy> vacancyStream, VacancyFilterDto filters);
}
