package faang.school.projectservice.filter.task;

import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.model.Task;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TaskPerformerUserFilter implements TaskFilter {
    @Override
    public Specification<Task> toSpecification(TaskFilterDto filter) {
        if (filter.getPerformerUserId() != null) {
            return (root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("performerUserId"), filter.getPerformerUserId());
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
    }
}
