package faang.school.projectservice.service.filter.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.service.filter.Filter;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

/**
 * VacancyPositionFilter — описание класса.
 * <p>
 * TODO: описать, какие обязанности у класса.
 * </p>
 *
 * @author Myrza
 * @since 21.07.2025
 */
@Component
public class VacancyPositionFilter implements Filter<Vacancy, VacancyFilterDto> {
    @Override
    public boolean isApplicable(VacancyFilterDto dto) {
        return dto.position() != null;
    }

    @Override
    public Stream<Vacancy> filter(Stream<Vacancy> entities, VacancyFilterDto dto) {
        var target = dto.position();
        return entities.filter(vacancy -> vacancy.getPosition().equals(target));
    }
}
