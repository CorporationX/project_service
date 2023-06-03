package faang.school.projectservice.service;

import faang.school.projectservice.dto.TaskDto;
import faang.school.projectservice.dto.filter.FilterDto;
import faang.school.projectservice.mapper.TaskMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.service.filter.Filter;
import faang.school.projectservice.service.filter.NameFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper mapper;
    private final List<Filter<Project>> filters;

    @Transactional(readOnly = true)
    public List<TaskDto> getTasksByProjectId(Long projectId) {
        return taskRepository.findAllByProjectId(projectId).stream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public boolean existsById(long id) {
        return taskRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public List<TaskDto> getTasksByFilter(FilterDto filterDto) {
        List<Task> tasks = taskRepository.findAll();

        for (Filter filter : filters) {
            if (filter instanceof NameFilter) {
                tasks = filter.applyFilter(tasks.stream().map(Task::getName), filterDto);
            }
        }

        return tasks.stream().map(mapper::toDto).collect(Collectors.toList());
    }
}
