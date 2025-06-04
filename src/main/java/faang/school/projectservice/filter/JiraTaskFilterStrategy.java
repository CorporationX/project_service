package faang.school.projectservice.filter;

import faang.school.projectservice.dto.jira.task.JiraTaskFilterDto;
import faang.school.projectservice.model.Task;

import java.util.stream.Stream;

public interface JiraTaskFilterStrategy {

    boolean isApplicable(JiraTaskFilterDto jiraTaskFilterDto);

    Stream<Task> apply(Stream<Task> tasks, JiraTaskFilterDto jiraTaskFilterDto);
}
