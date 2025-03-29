package faang.school.projectservice.filter;

import faang.school.projectservice.model.Task;

public interface TaskFilter {

    boolean test(Task task);
}
