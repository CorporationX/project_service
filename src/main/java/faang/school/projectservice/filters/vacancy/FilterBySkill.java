package faang.school.projectservice.filters.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public class FilterBySkill implements VacancyFilter {
    @Override
    public boolean isApplicable(VacancyFilterDto vacancyFilterDto) {
        return vacancyFilterDto.getSkillsPattern() != null;
    }

    @Override
    public Stream<Vacancy> apply(Stream<Vacancy> vacancies, VacancyFilterDto filterDto) {
        List<Long> filterPattern = filterDto.getSkillsPattern();
        return vacancies.filter(vacancy ->
                compareSkills(vacancy.getRequiredSkillIds(), filterPattern));
    }

    private boolean compareSkills(List<Long> vacancySkills, List<Long> requiredSkills) {
        // complexity O(N^2)
        for (Long requiredId : requiredSkills) {
            if (!vacancySkills.contains(requiredId)) {
                return false;
            }
        }
        return true;
    }
}
