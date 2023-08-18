package faang.school.projectservice.service;

import faang.school.projectservice.dto.task.CreateTaskDto;
import faang.school.projectservice.dto.task.ResponseTaskDto;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.task.TaskMapper;
import faang.school.projectservice.model.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final JiraApiService jiraApiService;
    private final TaskMapper taskMapper;

    @Transactional
    public ResponseTaskDto createTask(CreateTaskDto createTaskDto) {
        Task task = taskRepository.save(taskMapper.createDtoToEntity(createTaskDto));
        ResponseTaskDto response = taskMapper.entityToResponseDto(task);
        response.setJiraStatus(jiraApiService.createTask(createTaskDto));
        return response;
    }
}
