package faang.school.projectservice.service.filter.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.service.filter.Filter;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

/**
 * VacancyUpdateAtFilter — описание класса.
 * <p>
 * TODO: описать, какие обязанности у класса.
 * </p>
 *
 * @author Myrza
 * @since 21.07.2025
 */
@Component
public class VacancyUpdateAtFilter implements Filter<Vacancy, VacancyFilterDto> {
    @Override
    public boolean isApplicable(VacancyFilterDto dto) {
        return dto.updatedAtFrom() != null || dto.updatedAtTo() != null;
    }

    @Override
    public Stream<Vacancy> filter(Stream<Vacancy> entities, VacancyFilterDto dto) {
        var inDiapason = dto.updatedAtFrom() != null && dto.updatedAtTo() != null;
        var hasAtFrom = dto.updatedAtFrom() != null;
        return entities.filter(vacancy -> {
            var updatedAt = vacancy.getUpdatedAt();
            if (inDiapason) {
                return !dto.updatedAtFrom().isAfter(updatedAt)
                        && !dto.updatedAtTo().isBefore(updatedAt);
            }
            if (hasAtFrom) {
                return !dto.updatedAtFrom().isAfter(updatedAt);
            }
            return !dto.updatedAtTo().isBefore(updatedAt);
        });
    }
}
