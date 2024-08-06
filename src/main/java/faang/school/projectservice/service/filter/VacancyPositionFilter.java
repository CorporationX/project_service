package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.vacancy.filter.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class VacancyPositionFilter implements VacancyFilter {
    @Override
    public boolean isApplicable(VacancyFilterDto filters) {
        return filters.getProject() != null;
    }

    @Override
    public Stream<Vacancy> apply(Supplier<Stream<Vacancy>> vacancyStream, VacancyFilterDto filters) {
        return vacancyStream.get()
                .filter(vacancy -> vacancy.getProject().equals(filters.getProject()));
    }
}
