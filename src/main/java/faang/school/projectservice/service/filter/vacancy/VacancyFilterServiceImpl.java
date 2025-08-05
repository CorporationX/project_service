package faang.school.projectservice.service.filter.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.service.filter.Filter;
import faang.school.projectservice.service.filter.FilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * VacancyFilterServiceImpl — описание класса.
 * <p>
 * TODO: описать, какие обязанности у класса.
 * </p>
 *
 * @author Myrza
 * @since 21.07.2025
 */
@Component
@RequiredArgsConstructor
public class VacancyFilterServiceImpl implements FilterService<Vacancy, VacancyFilterDto> {
    private final List<Filter<Vacancy, VacancyFilterDto>> filters;

    @Override
    public List<Vacancy> getFilteredList(List<Vacancy> entities, VacancyFilterDto dto) {
        return applyFilters(filters, entities, dto);
    }
}
