package faang.school.projectservice.service;

import faang.school.projectservice.dto.sub_project.SubProjectCreateDto;
import faang.school.projectservice.dto.sub_project.SubProjectFilterDto;
import faang.school.projectservice.dto.sub_project.SubProjectUpdateDto;
import faang.school.projectservice.dto.sub_project.SubProjectViewDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.exception.NotFoundException;

import java.util.List;

/**
 * Сервис для работы с подпроектами
 * <p>
 * Предоставляет основные CRUD-операции для управления подпроектами:
 * <ul>
 *     <li>Создание новых подпроектов</li>
 *     <li>Обновление существующих подпроектов</li>
 *     <li>Получение отфильтрованных подпроектов</li>
 * </ul>
 * </p>
 *
 * <p>Все методы сервиса выбрасывают исключения в случаях:
 * <ul>
 *   <li>{@link NotFoundException} - если проект не найден</li>
 *   <li>{@link DataValidationException} - при нарушении бизнес-правил</li>
 *   <li>{@link ForbiddenException} - при попытке неавторизованных действий</li>
 * </ul>
 * </p>
 *
 *  Реализация {@link SubProjectServiceImpl}
 *
 * @author Linempy
 * @since 21.07.2025
 */
public interface SubProjectService {

    /**
     * Создает новый подпроект на основе предоставленных данных.
     *
     * @param createDto DTO с данными для создания подпроекта
     * @return DTO созданного подпроекта
     * @throws NotFoundException если родительский проект не существует
     * @throws DataValidationException если нарушены правила валидации
     * @throws ForbiddenException если нет прав на создание
     */
    SubProjectViewDto create(SubProjectCreateDto createDto);

    /**
     * Обновляет существующий подпроект.
     *
     * @param id идентификатор обновляемого подпроекта
     * @param updateDto DTO с обновляемыми полями
     * @return DTO обновленного подпроекта
     * @throws NotFoundException если подпроект не найден
     * @throws DataValidationException при невалидных изменениях
     * @throws ForbiddenException если нет прав на обновление
     */
    SubProjectViewDto update(Long id, SubProjectUpdateDto updateDto);

    /**
     * Возвращает список подпроектов с возможностью фильтрации.
     *
     * @param parentId идентификатор родительского проекта
     * @param filterDto параметры фильтрации (может быть null)
     * @return список DTO подпроектов, соответствующих критериям
     * @throws NotFoundException если родительский проект не существует
     */
    List<SubProjectViewDto> getByFilter(Long parentId, SubProjectFilterDto filterDto);
}