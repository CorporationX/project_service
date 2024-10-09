package faang.school.projectservice.filter.stage;

import faang.school.projectservice.dto.filter.stage.StageFilterDto;
import faang.school.projectservice.model.stage.Stage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StageNameFilterTest {

    private static final String NAME = "Test";
    @InjectMocks
    private StageNameFilter stageNameFilter;
    private StageFilterDto stageFilterDto;

    @Nested
    class PositiveTest {
        @Test
        @DisplayName("Возвращаем положительный результат")
        void whenValidateIsApplicableThenReturnTrue() {
            stageFilterDto = StageFilterDto.builder()
                    .stageName(NAME)
                    .build();

            assertTrue(stageNameFilter.isApplicable(stageFilterDto));
        }

        @Test
        @DisplayName("Возвращаем отфильтрованый список")
        void whenFilterThenReturn() {
            Stage stageOne = Stage.builder()
                    .stageName(NAME)
                    .build();

            Stage stageTwo = Stage.builder()
                    .stageName(NAME)
                    .build();

            stageFilterDto = StageFilterDto.builder()
                    .stageName(NAME)
                    .build();

            Stream<Stage> stageStream = Stream.of(stageOne, stageTwo);

            Stream<Stage> result = stageNameFilter.applyFilter(stageStream, stageFilterDto);

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
                    .stageName(null)
                    .build();

            assertFalse(stageNameFilter.isApplicable(stageFilterDto));
        }
    }
}