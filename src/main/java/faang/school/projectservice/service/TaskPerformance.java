package faang.school.projectservice.service;

import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class TaskPerformance {
    public Map<Long, List<Task>> mapPerformersUserIdsAndTasks(Internship internship) {
        List<Long> internsUserIds = internship.getInterns().stream()
                .map(TeamMember::getUserId)
                .toList();
        return internship.getProject().getTasks().stream()
                .filter(task -> internsUserIds.contains(task.getPerformerUserId()))
                .collect(Collectors.groupingBy(Task::getPerformerUserId));
    }

    public Map<Boolean, List<Long>> partitionByStatusDone(Internship internship) {
        Map<Long, List<Task>> map = mapPerformersUserIdsAndTasks(internship);
        return map.keySet().stream()
                .collect(Collectors.partitioningBy(key -> map.get(key).stream()
                        .allMatch(task -> task.getStatus().equals(TaskStatus.DONE))));
    }
}
