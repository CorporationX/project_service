package faang.school.projectservice.filter;

import faang.school.projectservice.model.Task;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PerformerFilter implements TaskFilter {

    private final Long performerId;

    @Override
    public boolean test(Task task) {
        return performerId == null || task.getPerformerUserId().equals(performerId);
    }
}
