package school.faang.projectservice.filter;

import faang.school.projectservice.filter.KeywordFilter;
import faang.school.projectservice.filter.PerformerFilter;
import faang.school.projectservice.filter.StatusFilter;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TaskFiltersTest {

    private Task testTask;

    @BeforeEach
    void setUp() {
        testTask = Task.builder()
                .name("Important task")
                .description("This is a very important task")
                .status(TaskStatus.IN_PROGRESS)
                .performerUserId(123L)
                .build();
    }

    @Test
    void testStatusFilter_Matches() {
        StatusFilter filter = new StatusFilter("in_progress");
        assertTrue(filter.test(testTask));
    }

    @Test
    void testStatusFilter_DoesNotMatch() {
        StatusFilter filter = new StatusFilter("done");
        assertFalse(filter.test(testTask));
    }

    @Test
    void testStatusFilter_NullStatus() {
        StatusFilter filter = new StatusFilter(null);
        assertTrue(filter.test(testTask));
    }

    @Test
    void testPerformerFilter_Matches() {
        PerformerFilter filter = new PerformerFilter(123L);
        assertTrue(filter.test(testTask));
    }

    @Test
    void testPerformerFilter_DoesNotMatch() {
        PerformerFilter filter = new PerformerFilter(456L);
        assertFalse(filter.test(testTask));
    }

    @Test
    void testPerformerFilter_NullPerformerId() {
        PerformerFilter filter = new PerformerFilter(null);
        assertTrue(filter.test(testTask));
    }

    @Test
    void testKeywordFilter_MatchesName() {
        KeywordFilter filter = new KeywordFilter("important");
        assertTrue(filter.test(testTask));
    }

    @Test
    void testKeywordFilter_MatchesDescription() {
        KeywordFilter filter = new KeywordFilter("very");
        assertTrue(filter.test(testTask));
    }

    @Test
    void testKeywordFilter_DoesNotMatch() {
        KeywordFilter filter = new KeywordFilter("urgent");
        assertFalse(filter.test(testTask));
    }

    @Test
    void testKeywordFilter_NullKeyword() {
        KeywordFilter filter = new KeywordFilter(null);
        assertTrue(filter.test(testTask));
    }
}
