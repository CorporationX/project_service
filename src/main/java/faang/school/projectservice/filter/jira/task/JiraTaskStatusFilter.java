package faang.school.projectservice.filter.jira.task;

import faang.school.projectservice.dto.jira.task.JiraTaskFilterDto;
import faang.school.projectservice.filter.JiraTaskFilterStrategy;
import faang.school.projectservice.model.Task;

import java.util.Objects;
import java.util.stream.Stream;

public class JiraTaskStatusFilter implements JiraTaskFilterStrategy {

    @Override
    public boolean isApplicable(JiraTaskFilterDto jiraTaskFilterDto) {
        return jiraTaskFilterDto.getStatus() != null;
    }

    @Override
    public Stream<Task> apply(Stream<Task> tasks, JiraTaskFilterDto jiraTaskFilterDto) {
        return tasks.filter(task ->
                Objects.equals(task.getStatus(), jiraTaskFilterDto.getStatus()));
    }
}
