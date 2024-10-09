package faang.school.projectservice.filter.stage;

import faang.school.projectservice.dto.filter.stage.StageFilterDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StageTaskStatusFilterTest {

    private static final TaskStatus STATUS_TESTING = TaskStatus.TESTING;
    private static final TaskStatus STATUS_DONE = TaskStatus.DONE;
    private static final List<TaskStatus> TASK_STATUSES = new ArrayList<>();
    @InjectMocks
    private StageTaskStatusFilter stageTaskStatusFilter;
    private StageFilterDto stageFilterDto;

    @BeforeEach
    void init() {
        TASK_STATUSES.add(STATUS_TESTING);
        TASK_STATUSES.add(STATUS_DONE);

        stageFilterDto = StageFilterDto.builder()
                .taskStatuses(TASK_STATUSES)
                .build();
    }

    @Nested
    class PositiveTest {
        @Test
        @DisplayName("Возвращаем положительный результат")
        void whenValidateIsApplicableThenReturnTrue() {
            assertTrue(stageTaskStatusFilter.isApplicable(stageFilterDto));
        }

        @Test
        @DisplayName("Возвращаем отфильтрованый список")
        void whenFilterThenReturn() {
            Task taskOne = new Task();
            taskOne.setStatus(STATUS_TESTING);

            Task taskTwo = new Task();
            taskTwo.setStatus(STATUS_DONE);

            List<Task> tasks = new ArrayList<>(List.of(taskOne, taskTwo));

            Stage stageOne = Stage.builder()
                    .tasks(tasks)
                    .build();

            Stage stageTwo = Stage.builder()
                    .tasks(tasks)
                    .build();

            Stream<Stage> stageStream = Stream.of(stageOne, stageTwo);

            Stream<Stage> result = stageTaskStatusFilter.applyFilter(stageStream, stageFilterDto);

            assertNotNull(result);
            assertEquals(result.count(), 2);
        }
    }

    @Nested
    class NegativeTest {
        @Test
        @DisplayName("Возвращаем отрицательный результат")
        void whenValidateIsApplicableThenReturnFalse() {
            stageFilterDto = StageFilterDto.builder()
                    .taskStatuses(null)
                    .build();

            assertFalse(stageTaskStatusFilter.isApplicable(stageFilterDto));

            stageFilterDto = StageFilterDto.builder()
                    .taskStatuses(new ArrayList<>())
                    .build();

            assertFalse(stageTaskStatusFilter.isApplicable(stageFilterDto));
        }
    }
}