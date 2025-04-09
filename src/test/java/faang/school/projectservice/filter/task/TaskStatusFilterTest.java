package faang.school.projectservice.filter.task;

import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskStatusFilterTest {

    private final TaskStatusFilter filter = new TaskStatusFilter();
    private final TaskStatus firstStatus = TaskStatus.IN_PROGRESS;

    @Mock
    private Root<Task> root;

    @Mock
    private CriteriaQuery<Task> query;

    @Mock
    private CriteriaBuilder builder;

    @Mock
    private Path<TaskStatus> statusPath;

    @Mock
    private Predicate statusPredicate;

    @Test
    public void testPositiveApplicable() {
        boolean isApplicable = filter.isApplicable(createFilterDto(firstStatus));

        assertTrue(isApplicable);
    }

    @Test
    public void testPositiveApplicableWhenFilterNull() {
        boolean isApplicable = filter.isApplicable(createFilterDto(null));

        assertFalse(isApplicable);
    }

    @Test
    public void testPositiveApply() {
        TaskFilterDto filterDto = createFilterDto(firstStatus);
        when(root.<TaskStatus>get("status")).thenReturn(statusPath);
        when(builder.equal(statusPath, firstStatus)).thenReturn(statusPredicate);

        Predicate result = filter.apply(filterDto).toPredicate(root, query, builder);

        assertEquals(result, statusPredicate);
    }

    private TaskFilterDto createFilterDto(TaskStatus status) {
        return TaskFilterDto.builder()
                .status(status)
                .build();
    }
}
