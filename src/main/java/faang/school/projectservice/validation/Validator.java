package faang.school.projectservice.validation;

/**
 * Универсальный интерфейс для валидации объектов (DTO).
 * <p>
 * Реализации этого интерфейса должны содержать логику проверки данных
 * для конкретного типа DTO. Обычно используется для более сложной
 * валидации, которую невозможно выразить с помощью стандартных
 * аннотаций Bean Validation.
 *
 * @param <D> тип DTO, который требуется валидировать
 * @author Myrza
 * @since 31.07.2025
 */
public interface Validator<D> {
    void validate(D dto);
}
