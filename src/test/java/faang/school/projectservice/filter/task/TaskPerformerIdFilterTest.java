package faang.school.projectservice.filter.task;

import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.model.Task;
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
public class TaskPerformerIdFilterTest {

    private final TaskPerformerIdFilter filter = new TaskPerformerIdFilter();
    private final Long firstId = 1L;

    @Mock
    private Root<Task> root;

    @Mock
    private CriteriaQuery<Task> query;

    @Mock
    private CriteriaBuilder builder;

    @Mock
    private Path<Long> performerPath;

    @Mock
    private Predicate performerPredicate;

    @Test
    public void testPositiveApplicable() {
        boolean isApplicable = filter.isApplicable(createFilterDto(firstId));

        assertTrue(isApplicable);
    }

    @Test
    public void testPositiveApplicableWhenFilterNull() {
        boolean isApplicable = filter.isApplicable(createFilterDto(null));

        assertFalse(isApplicable);
    }

    @Test
    public void testPositiveApply() {
        TaskFilterDto filterDto = createFilterDto(firstId);
        when(root.<Long>get("performerUserId")).thenReturn(performerPath);
        when(builder.equal(performerPath, firstId)).thenReturn(performerPredicate);

        Predicate result = filter.apply(filterDto).toPredicate(root, query, builder);

        assertEquals(result, performerPredicate);
    }

    private TaskFilterDto createFilterDto(Long performerId) {
        return TaskFilterDto.builder()
                .performerId(performerId)
                .build();
    }
}
