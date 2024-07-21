package faang.school.projectservice.filter.moment;


import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.stream.Stream;

import static faang.school.projectservice.util.TestMoment.FILTERED_MOMENTS;
import static faang.school.projectservice.util.TestMoment.MOMENTS;
import static faang.school.projectservice.util.TestMoment.NOW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class MomentDateFilterTest {
    private final MomentDateFilter momentDateFilter = new MomentDateFilter();

    @Test
    public void testShouldReturnTrueIfFilterSpecified() {
        MomentFilterDto momentFilterDto = MomentFilterDto.builder()
                .startDate(NOW)
                .endDate(NOW)
                .build();
        boolean isApplicable = momentDateFilter.isApplicable(momentFilterDto);
        assertTrue(isApplicable);
    }

    @Test
    public void testShouldReturnFalseIsStartDateNotSpecified() {
        MomentFilterDto momentFilterDto = MomentFilterDto.builder()
                .endDate(NOW)
                .build();
        boolean isApplicable = momentDateFilter.isApplicable(momentFilterDto);
        assertFalse(isApplicable);
    }

    @Test
    public void testShouldReturnFalseIsEndDateNotSpecified() {
        MomentFilterDto momentFilterDto = MomentFilterDto.builder()
                .startDate(NOW)
                .build();
        boolean isApplicable = momentDateFilter.isApplicable(momentFilterDto);
        assertFalse(isApplicable);
    }

    @Test
    public void testShouldReturnMomentFallingWithinDateRange() {
        MomentFilterDto momentFilterDto = MomentFilterDto.builder()
                .startDate(NOW.minusHours(1))
                .endDate(NOW.plusHours(1))
                .build();

        Stream<Moment> filteredMoments = momentDateFilter.apply(() -> MOMENTS.stream(), momentFilterDto);
        assertEquals(FILTERED_MOMENTS, filteredMoments.toList());
    }

    @Test
    public void testShouldNotReturnMomentNotWithinDateRange() {
        MomentFilterDto momentFilterDto = MomentFilterDto.builder()
                .startDate(NOW.minusDays(2))
                .endDate(NOW.minusDays(1))
                .build();
        Stream<Moment> filteredMoment = momentDateFilter.apply(() -> MOMENTS.stream(), momentFilterDto);
        assertEquals(Collections.emptyList(), filteredMoment.toList());
    }
}
