package faang.school.projectservice.service;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.dto.TaskDto;
import faang.school.projectservice.mapper.TaskMapper;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.service.filter.TaskFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final TaskFilter filter;

    @Transactional(readOnly = true)
    public List<TaskDto> getTasksByProjectId(Long projectId) {
        return taskRepository.findAllByProjectId(projectId).stream()
                .map(taskMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public boolean existsById(long id) {
        return taskRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public List<TaskDto> getTasksByFilter(ProjectFilterDto filterDto) {
        Stream<Task> tasks = taskRepository.findAll().stream();
        return filter.applyFilter(tasks, filterDto).map(taskMapper::toDto).collect(Collectors.toList());
    }

}
