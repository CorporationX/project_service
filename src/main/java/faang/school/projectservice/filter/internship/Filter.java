package faang.school.projectservice.filter.internship;

import faang.school.projectservice.dto.filter.FilterDto;

import java.util.stream.Stream;

/**
 * Whether you can or not, you're right anyway (c) Henry Ford
 *
 * @param <T> FilterDto - Общий абстрактный класс для наследования любых *SomeName*FilterDto
 * @param <R> Сущности по которым ведем фильтрацию
 * При создании любого фильтра наследуем "Your"FilterDto от FilterDto, далее наслдуемся от этого интерфейса
 *         ***  ...YourFilter implements Filter<YourFilterDto, YourEntityToFilter> ***
 *           enjoy!
 */

public interface Filter<T extends FilterDto, R> {

    boolean isApplicable(T filters);

    Stream<R> applyFilter(Stream<R> entities, T filters);
}
