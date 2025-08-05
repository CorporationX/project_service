package faang.school.projectservice.service.internship;

import faang.school.projectservice.apimodel.InternshipDto;
import faang.school.projectservice.apimodel.InternshipFilterDto;

import java.util.List;

/**
 * Сервис для управления стажировками в рамках проекта.
 * <p>
 * Предоставляет методы для создания, обновления, получения и фильтрации стажировок.
 * Реализация инкапсулирует бизнес-логику, связанную с проектными стажировками.
 * </p>
 * <p>
 * Основные обязанности:
 * <ul>
 *     <li>Создание и обновление стажировок</li>
 *     <li>Получение списка всех стажировок</li>
 *     <li>Поиск стажировки по идентификатору</li>
 *     <li>Фильтрация стажировок по параметрам проекта</li>
 * </ul>
 *
 * @author agent
 * @since 04.08.2025
 */
public interface InternshipService {

    /**
     * Создаёт новую стажировку в рамках указанного проекта.
     *
     * @param projectId идентификатор проекта
     * @param dto       данные стажировки
     * @return созданная стажировка
     */
    InternshipDto create(Long projectId, InternshipDto dto);

    /**
     * Обновляет существующую стажировку.
     *
     * @param id  идентификатор стажировки
     * @param dto обновлённые данные
     * @return обновлённая стажировка
     */
    InternshipDto update(Long id, InternshipDto dto);

    /**
     * Возвращает список всех стажировок.
     *
     * @return список всех стажировок
     */
    List<InternshipDto> findAll();

    /**
     * Возвращает стажировку по её идентификатору.
     *
     * @param id идентификатор стажировки
     * @return найденная стажировка
     */
    InternshipDto findById(Long id);

    /**
     * Выполняет фильтрацию стажировок по проекту и дополнительным параметрам.
     *
     * @param filterDto фильтр с параметрами проекта
     * @return список подходящих стажировок
     */
    List<InternshipDto> findByProject(InternshipFilterDto filterDto);
}