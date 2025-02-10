package faang.school.projectservice.service.impl;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MomentProjectsFilterTest {
    private MomentFilterDto momentFilterDto;
    private final MomentProjectsFilter filter = new MomentProjectsFilter();
    private List<Moment> allMoments;
    @BeforeEach
    void setUp() {
        allMoments = TestData.getSomeMoments();
    }

    @Test
    @DisplayName("Test applicability moment filter by projects")
    void testApplicability() {
        momentFilterDto = MomentFilterDto.builder()
                .projectsIds(List.of(12L))
                .build();
        assertTrue(filter.isApplicable(momentFilterDto));

        momentFilterDto = MomentFilterDto.builder()
                .projectsIds(List.of(12L, 13L))
                .build();
        assertTrue(filter.isApplicable(momentFilterDto));

        momentFilterDto = MomentFilterDto.builder()
                .projectsIds(List.of())
                .build();
        assertFalse(filter.isApplicable(momentFilterDto));
    }

    @Test
    @DisplayName("Test applying filter")
    void apply() {
        momentFilterDto = MomentFilterDto.builder()
                .projectsIds(List.of(12L, 13L))
                .build();
        Stream<Moment> momentStream = filter.apply(allMoments.stream(), momentFilterDto);
        List<Moment> filteredMoments = momentStream.toList();
        Assertions.assertEquals(1, filteredMoments.size());
        Assertions.assertEquals(2L, filteredMoments.get(0).getId());
    }
}