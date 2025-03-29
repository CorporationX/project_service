package faang.school.projectservice.filter;

import faang.school.projectservice.model.Task;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StatusFilter implements TaskFilter {

    private final String status;

    @Override
    public boolean test(Task task) {
        if (status == null || status.isEmpty()) return true;
        return task.getStatus().name().equalsIgnoreCase(status);
    }
}
