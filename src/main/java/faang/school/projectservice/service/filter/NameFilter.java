package faang.school.projectservice.service.filter;

import faang.school.projectservice.model.Vacancy;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NameFilter implements VacancyFilter {
    private final String name;
    @Override public boolean test(Vacancy vacancy) {
        return name == null || vacancy.getName().contains(name);
    }
}
