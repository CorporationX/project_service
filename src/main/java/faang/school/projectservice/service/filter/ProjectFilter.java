package faang.school.projectservice.service.filter;

import faang.school.projectservice.model.Vacancy;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProjectFilter implements VacancyFilter {
private final long projectId;

    @Override
    public boolean test(Vacancy vacancy) {
        return projectId == vacancy.getId();
    }
}
