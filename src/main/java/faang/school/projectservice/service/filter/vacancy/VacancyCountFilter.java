package faang.school.projectservice.service.filter.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.service.filter.Filter;

import java.util.stream.Stream;

/**
 * VacancyCountFIlter — описание класса.
 * <p>
 * TODO: описать, какие обязанности у класса.
 * </p>
 *
 * @author Myrza
 * @since 21.07.2025
 */
public class VacancyCountFilter implements Filter<Vacancy, VacancyFilterDto> {
    @Override
    public boolean isApplicable(VacancyFilterDto dto) {
        return dto.minCount() != null || dto.maxCount() != null;
    }

    @Override
    public Stream<Vacancy> filter(Stream<Vacancy> entities, VacancyFilterDto dto) {
        var min = dto.minCount() != null ? dto.minCount() : 0;
        var max = dto.maxCount() != null ? dto.maxCount() : Integer.MAX_VALUE;
        return entities.filter(vacancy -> min <= vacancy.getCount() && vacancy.getCount() <= max);
    }
}
