package faang.school.projectservice.service.vacancy.filter;

import faang.school.projectservice.dto.vacancy.filter.VacancyFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.Vacancy;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.stream.Stream;

@Component
public class VacancySkillsFilter implements Filter<VacancyFilterDto, Vacancy> {
    @Override
    public boolean isApplicable(VacancyFilterDto filters) {
        return filters.getSkillIds() != null;
    }

    @Override
    public Stream<Vacancy> apply(Stream<Vacancy> vacancies, VacancyFilterDto filters) {
        return vacancies.filter(vacancy ->
                new HashSet<>(vacancy.getRequiredSkillIds())
                        .containsAll(filters.getSkillIds()));
    }
}
