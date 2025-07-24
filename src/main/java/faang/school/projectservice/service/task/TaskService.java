package faang.school.projectservice.service.task;

import faang.school.projectservice.dto.client.task.TaskCreateDto;
import faang.school.projectservice.dto.client.task.TaskUpdateDto;
import faang.school.projectservice.dto.client.task.TaskViewDto;

import java.util.List;

public interface TaskService {

    TaskViewDto createTask(TaskCreateDto createDto);

    TaskViewDto updateTask(TaskUpdateDto updateDto);

    List<TaskViewDto> getByFilter();

    TaskViewDto getById();
}
