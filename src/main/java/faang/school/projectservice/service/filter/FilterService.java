package faang.school.projectservice.service.filter;

import java.util.List;
import java.util.stream.Stream;

/**
 * Сервис для применения набора фильтров к коллекции сущностей по переданным критериям фильтрации
 * <p>
 * Предназначен для взаимодействием с коллекцией реализацией интерфейса {@link Filter},
 * который последовательно отфильтровывают список сущностей {@code E} по критериям {@code D}
 * </p>
 *
 * Сценарий использования:
 * <p>
 *     <ul>
 *         <li>Есть несколько реализаций {@link Filter}, каждая из который
 *         знает когда она применима (через {@code isApplicable}) и как фильтровать ({@code apply})</li>
 *         <li>{@code FilterService} проходит по списку фильтров, выбирает применимые
 *         и применяет их к списку сущностей по цепочке</li>
 *     </ul>
 * </p>
 *
 * @param <E> тип сущности, которая фильтруется
 * @param <D> тип объекта с параметрами фильтрации
 *
 * @author Linempy
 * @since 23.07.2025
 */
public interface FilterService<E, D> {

    /**
     * Применяется для получения списка отфильтрованных сущностей
     * <p>
     *     Применяет цепочку фильтров к списку сущностей на основе параметров фильтрации
     *     Обычно (default) реализуется как последовательное применения всех фильтров,
     *     для который метод {@code Filter.isApplicable(D dto)} является {@code true}
     *     Каждый применимый фильтр обрабатывает результат предыдущего шага
     * </p>
     *
     * @param entities список сущностей, которые нужно отфильтровать
     * @param dto объект с параметрами фильтрации
     * @return отфильтрованный список сущностей
     */
    List<E> getFilteredList(List<E> entities, D dto);


    /**
     * Применяет цепочку фильтров к списку сущностей
     * <p>
     *     Проверяет входные данные на null и пустоту, после
     *     последовательно применяет фильтры, проходящие проверку {@code Filter.isApplicable(D dto)}
     * </p>
     *
     * @param filters список фильтров
     * @param entities список сущностей для фильтрации
     * @param dto объект с параметрами фильтрации
     * @return отфильтрованный список сущностей
     */
    default List<E> applyFilters(List<Filter<E, D>> filters, List<E> entities, D dto) {
        if (entities == null || entities.isEmpty()) {
            return List.of();
        }
        Stream<E> stream = entities.stream();
        for (Filter<E, D> filter : filters) {
            if (filter.isApplicable(dto)) {
                stream = filter.apply(stream, dto);
            }
        }

        return stream.toList();
    }
}