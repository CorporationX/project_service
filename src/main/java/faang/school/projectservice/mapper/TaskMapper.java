package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.task.CreateTaskDto;
import faang.school.projectservice.dto.task.TaskResult;
import faang.school.projectservice.dto.task.UpdateTaskDto;
import faang.school.projectservice.model.Task;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TaskMapper {
     String JIRA_BASE_URL = "https://faang-school.atlassian.ne/browse/";

    void updateTaskFromDto(UpdateTaskDto dto, @MappingTarget Task task);

    Task toTask(CreateTaskDto createTaskDto);

    @Mapping(target = "jiraTaskUrl", expression = "java(mapJiraTaskUrl(task))")
    TaskResult toDto(Task task);

    default String mapJiraTaskUrl(Task task) {
        if (task.getJiraKey() != null) {
            return JIRA_BASE_URL + task.getJiraKey();
        }
        return null;
    }
}
