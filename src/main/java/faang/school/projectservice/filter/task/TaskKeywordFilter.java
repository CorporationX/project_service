package faang.school.projectservice.filter.task;

import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.model.Task;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TaskKeywordFilter implements TaskFilter {

    @Override
    public boolean isApplicable(TaskFilterDto filter) {
        return filter.keyword() != null && !filter.keyword().isBlank();
    }

    @Override
    public Specification<Task> apply(TaskFilterDto filter) {
        String pattern = "%" + filter.keyword().toLowerCase()
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_") + "%";

        return ((root, query, builder) ->
                builder.or(
                        builder.like(builder.lower(root.get("name")), pattern, '\\'),
                        builder.like(builder.lower(root.get("description")), pattern, '\\')
                ));
    }
}
