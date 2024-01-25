package faang.school.projectservice.filter.stage;

import faang.school.projectservice.dto.stage.StageFilterDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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

        //Act

        //Assert
    }
}