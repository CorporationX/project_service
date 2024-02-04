package faang.school.projectservice.filter.stage;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class StageNameFilterTest {

    private StageNameFilter stageNameFilter = new StageNameFilter();
    private StageFilterDto filter;

    @BeforeEach
    void setUp() {
        filter = StageFilterDto.builder().stageNamePattern("Stage").build();

    }

    @Test
    void testIsApplicable() {
        //Act & Assert
        assertTrue(stageNameFilter.isApplicable(filter));
    }

    @Test
    void testApply() {
        //Arrange
        Stage stage = Stage.builder().stageName("Stage").build();
        Stream<Stage> stageStream = List.of(stage).stream();

        //Act
        List<Stage> result = stageNameFilter.apply(stageStream, filter).toList();

        //Assert
        assertTrue(result.contains(stage));
    }
}