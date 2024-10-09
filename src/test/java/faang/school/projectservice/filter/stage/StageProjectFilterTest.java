package faang.school.projectservice.filter.stage;

import faang.school.projectservice.dto.filter.stage.StageFilterDto;
import faang.school.projectservice.model.Project;
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
class StageProjectFilterTest {

    private static final Long ID = 1L;
    @InjectMocks
    private StageProjectFilter stageProjectFilter;
    private StageFilterDto stageFilterDto;

    @Nested
    class PositiveTest {
        @Test
        @DisplayName("Возвращаем положительный результат")
        void whenValidateIsApplicableThenReturnTrue() {
            stageFilterDto = StageFilterDto.builder()
                    .projectId(ID)
                    .build();

            assertTrue(stageProjectFilter.isApplicable(stageFilterDto));
        }

        @Test
        @DisplayName("Возвращаем отфильтрованый список")
        void whenFilterThenReturn() {
            Project projectOne = Project.builder()
                    .id(ID)
                    .build();

            Stage stageOne = Stage.builder()
                    .project(projectOne)
                    .build();

            stageFilterDto = StageFilterDto.builder()
                    .projectId(ID)
                    .build();

            Stream<Stage> stageStream = Stream.of(stageOne);

            Stream<Stage> result = stageProjectFilter.applyFilter(stageStream, stageFilterDto);

            assertNotNull(result);
            assertEquals(result.count(), 1);
        }
    }

    @Nested
    class NegativeTest {
        @Test
        @DisplayName("Возвращаем отрицательный результат")
        void whenValidateIsApplicableThenReturnFalse() {
            stageFilterDto = StageFilterDto.builder()
                    .projectId(null)
                    .build();

            assertFalse(stageProjectFilter.isApplicable(stageFilterDto));
        }
    }
}