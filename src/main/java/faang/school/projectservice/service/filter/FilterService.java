package faang.school.projectservice.service.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Сервис для применения набора фильтров к коллекции сущностей на основе переданных параметров фильтрации.
 * <p>
 * Предназначен для организации работы с коллекцией реализаций интерфейса {@link Filter},
 * которые могут последовательно отфильтровывать список сущностей {@code E} в соответствии с параметрами {@code D}.
 * </p>
 * <p>
 * Типичный сценарий использования:
 * <ul>
 *     <li>Есть несколько реализаций {@link Filter}, каждая из которых знает,
 *     когда она применима (через {@code isApplicable}) и как фильтровать.</li>
 *     <li>{@code FilterService} проходит по списку фильтров, выбирает
 *     применимые и применяет их к коллекции сущностей по цепочке.</li>
 * </ul>
 * </p>
 *
 * @param <E> тип сущности, подлежащей фильтрации
 * @param <D> тип объекта фильтрации с параметрами фильтрации
 */
public interface FilterService<E, D> {
    /**
     * Применяет цепочку фильтров к списку сущностей на основе параметров фильтрации.
     * <p>
     * Обычно реализуется как последовательное применение всех фильтров,
     * для которых метод {@link Filter#isApplicable(Object)} возвращает {@code true}.
     * Каждый применимый фильтр обрабатывает результат предыдущего шага.
     * </p>
     *
     * @param entities список сущностей, которые нужно отфильтровать
     * @param dto      параметры фильтрации
     * @return новый список сущностей, прошедших фильтрацию
     */
    List<E> getFilteredList(List<E> entities, D dto);

    /**
     * Применяет цепочку фильтров к списку сущностей.
     * <p>
     * Проверяет входные данные на null и пустоту,
     * затем последовательно применяет только те фильтры,
     * которые подходят по критериям {@link Filter#isApplicable(Object)}.
     * </p>
     *
     * @param entities список сущностей для фильтрации
     * @param dto параметры фильтрации
     * @param filters список фильтров
     * @return отфильтрованный список сущностей
     */
    default List<E> applyFilters(List<Filter<E, D>> filters, List<E> entities, D dto) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }

        Stream<E> stream = entities.stream();
        for (Filter<E, D> filter : filters) {
            if (filter.isApplicable(dto)) {
                stream = filter.filter(stream, dto);
            }
        }

        return stream.toList();
    }
}