package faang.school.projectservice.service.filter.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.service.filter.Filter;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

/**
 * VacancyCreateAtFilter — описание класса.
 * <p>
 * TODO: описать, какие обязанности у класса.
 * </p>
 *
 * @author Myrza
 * @since 21.07.2025
 */
@Component
public class VacancyCreateAtFilter implements Filter<Vacancy, VacancyFilterDto> {
    @Override
    public boolean isApplicable(VacancyFilterDto dto) {
        return dto.createdAtFrom() != null || dto.createdAtTo() != null;
    }

    @Override
    public Stream<Vacancy> filter(Stream<Vacancy> entities, VacancyFilterDto dto) {
        var inDiapason = dto.createdAtFrom() != null && dto.createdAtTo() != null;
        var hasAtFrom = dto.createdAtFrom() != null;
        return entities.filter(vacancy -> {
            var createdAt = vacancy.getCreatedAt();
            if (inDiapason) {
                return !dto.createdAtFrom().isAfter(createdAt)
                        && !dto.createdAtTo().isBefore(createdAt);
            }
            if (hasAtFrom) {
                return !dto.createdAtFrom().isAfter(createdAt);
            }
            return !dto.createdAtTo().isBefore(createdAt);
        });
    }
}
