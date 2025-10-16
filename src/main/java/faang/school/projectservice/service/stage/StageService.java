package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageCreateDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.dto.stage.StageUpdateDto;
import faang.school.projectservice.dto.stage.StageViewDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.stage.enums.DeleteStrategy;

import java.util.List;

/**
 * Сервис для управления этапами проекта.
 * <p>
 * Предоставляет методы для создания, обновления, удаления и получения информации
 * о этапах проекта, включая фильтрацию и управление стратегиями удаления.
 * </p>
 * @author bozya
 * @since 31.07.2025
 */
public interface StageService {
    /**
     * Создает новый этап проекта.
     *
     * @param stageCreateDto DTO с данными для создания этапа
     * @return DTO созданного этапа
     * @throws DataValidationException если данные не прошли валидацию
     * @throws EntityNotFoundException если связанный проект не найден
     */
    StageViewDto create(StageCreateDto stageCreateDto);

    /**
     * Возвращает список этапов с учетом фильтров.
     *
     * @param filters параметры фильтрации (название, статус, даты и т.д.)
     * @return список DTO этапов, соответствующих критериям фильтрации
     */
    List<StageViewDto> getAllStagesWithFilters(StageFilterDto filters);

    /**
     * Удаляет этап проекта с использованием указанной стратегии.
     *
     * @param stageId ID удаляемого этапа
     * @param strategy стратегия удаления (CASCADE, CLOSE, MOVE)
     * @throws EntityNotFoundException если этап с указанным ID не найден
     */
    void delete(Long stageId, DeleteStrategy strategy);

    /**
     * Обновляет данные этапа проекта.
     *
     * @param updateDto DTO с обновленными данными
     * @param stageId ID обновляемого этапа
     * @return DTO обновленного этапа
     * @throws EntityNotFoundException если этап с указанным ID не найден
     * @throws DataValidationException если новые данные не прошли валидацию
     */
    StageViewDto update(StageUpdateDto updateDto, Long stageId);

    /**
     * Получает этап проекта по ID.
     *
     * @param id ID запрашиваемого этапа
     * @return DTO этапа проекта
     * @throws EntityNotFoundException если этап с указанным ID не найден
     */
    StageViewDto getById(Long id);

    /**
     * Получает все этапы указанного проекта.
     *
     * @param id ID проекта
     * @return список DTO этапов проекта
     * @throws EntityNotFoundException если проект с указанным ID не найден
     */
    List<StageViewDto> getAllStages(Long id);
}