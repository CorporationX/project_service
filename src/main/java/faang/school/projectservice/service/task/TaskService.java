package faang.school.projectservice.service.task;

import faang.school.projectservice.dto.client.task.TaskCreateDto;
import faang.school.projectservice.dto.client.task.TaskFilterDto;
import faang.school.projectservice.dto.client.task.TaskUpdateDto;
import faang.school.projectservice.dto.client.task.TaskViewDto;

import java.util.List;

/**
 * Интерфейс, предоставляющий операции по созданию, обновлению задачи и получению списка задач
 * с фильтрами (по статусу, исполнителю или ключевому слову), без фильтров и задачи по айди.
 *
 * @author mrnght
 * @since 27.07.2025
 */
public interface TaskService {

    /**
     * Создание задачи
     *
     * @param createDto передаваемые данные, необходимые для создания задачи
     */
    TaskViewDto createTask(TaskCreateDto createDto);

    /**
     * Обновление проекта
     *
     * @param id айди обновляемой задачи
     * @param updateDto передаваемые данные, необходимые для обновления задачи
     * @return
     */
    TaskViewDto updateTask(long id, TaskUpdateDto updateDto);

    /**
     * Возвращает список задач, соответствующих указанным фильтрам.
     *
     * @param taskFilterDto параметры фильтрации
     * @return список задач в виде List<{@link TaskViewDto}>
     */
    List<TaskViewDto> getByFilter(TaskFilterDto taskFilterDto);

    /**
     * Получение задачи по её id
     *
     * @param id принимаемый id задачи
     * @return {@link TaskViewDto} возвращаемая задача
     */
    TaskViewDto getById(long id);
}
