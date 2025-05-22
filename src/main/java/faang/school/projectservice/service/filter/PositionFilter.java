package faang.school.projectservice.service.filter;

import faang.school.projectservice.model.Vacancy;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PositionFilter implements VacancyFilter {
    private final String position;

    @Override
    public boolean test(Vacancy vacancy) {
        return position == null || vacancy.getPosition().name().equalsIgnoreCase(position);
    }
}
