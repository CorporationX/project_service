package faang.school.projectservice.filter;

import faang.school.projectservice.dto.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;
import java.util.stream.Stream;

@Component
public class VacancyNameFilter implements VacancyFilter {
    @Override
    public boolean isApplicable(VacancyFilterDto filters) {
        return !filters.getName().isBlank();
    }

    @Override
    public Stream<Vacancy> apply(Supplier<Stream<Vacancy>> vacancies, VacancyFilterDto filters) {
        return vacancies.get().filter(vacancy -> vacancy.getName().equals(filters.getName()));
    }
}
