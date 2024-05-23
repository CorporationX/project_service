package faang.school.projectservice.filter;

import faang.school.projectservice.dto.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Component
public class VacancySkillFilter implements VacancyFilter {
    @Override
    public boolean isApplicable(VacancyFilterDto filters) {
        return filters.getSkillIds() != null && !filters.getSkillIds().isEmpty();
    }

    @Override
    public Stream<Vacancy> apply(Supplier<Stream<Vacancy>> vacancies, VacancyFilterDto filters) {
        return vacancies.get().filter(vacancy -> new HashSet<>(vacancy.getRequiredSkillIds()).containsAll(filters.getSkillIds()));
    }
}
