package faang.school.projectservice.validation.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.validation.Validator;

/**
 * Валидатор для {@link VacancyFilterDto}.
 * <p>
 * Выполняет следующие проверки:
 * <ul>
 *   <li>DTO не является {@code null}</li>
 *   <li>{@code minCount} не больше, чем {@code maxCount}</li>
 *   <li>{@code createdAtFrom} не позже, чем {@code createdAtTo}</li>
 *   <li>{@code updatedAtFrom} не позже, чем {@code updatedAtTo}</li>
 * </ul>
 * Если какая-либо проверка не пройдена, выбрасывается исключение {@link DataValidationException}.
 *
 * @author Myrza
 * @since 03.08.2025
 */
public class VacancyFilterDtoValidator implements Validator<VacancyFilterDto> {
    public void validate(VacancyFilterDto dto) {
        if (dto == null) {
            throw new DataValidationException("Дата пустая");
        }
        if (dto.minCount() != null && dto.maxCount() != null && dto.minCount() > dto.maxCount()) {
            throw new DataValidationException("Минимальное количество вакансии не может быть больше максимальной");
        }

        if (dto.createdAtFrom() != null && dto.createdAtTo() != null
                && dto.createdAtFrom().isAfter(dto.createdAtTo())) {
            throw new DataValidationException("Диапазон даты создания задан не корректно");
        }

        if (dto.updatedAtFrom() != null && dto.updatedAtTo() != null
                && dto.updatedAtFrom().isAfter(dto.updatedAtTo())) {
            throw new DataValidationException("Диапазон даты обновления задан не корректно");
        }
    }
}
