package faang.school.projectservice.service.task;

import faang.school.projectservice.dto.client.task.TaskCreateDto;
import faang.school.projectservice.dto.client.task.TaskFilterDto;
import faang.school.projectservice.dto.client.task.TaskUpdateDto;
import faang.school.projectservice.dto.client.task.TaskViewDto;

import java.util.List;

public interface TaskService {

    TaskViewDto createTask(TaskCreateDto createDto);

    TaskViewDto updateTask(long id, TaskUpdateDto updateDto);

    List<TaskViewDto> getByFilter(TaskFilterDto taskFilterDto, Long projectId);

    TaskViewDto getById(long id);
}
