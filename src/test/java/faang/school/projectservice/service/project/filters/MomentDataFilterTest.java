package faang.school.projectservice.service.project.filters;

import faang.school.projectservice.dto.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static faang.school.projectservice.util.TestMoment.FILTERED_MOMENTS;
import static faang.school.projectservice.util.TestMoment.MOMENTS;
import static faang.school.projectservice.util.TestMoment.NOW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MomentDataFilterTest {

    private final MomentDataFilter momentDataFilter = new MomentDataFilter();

    @Test
    public void testShouldReturnTrueIfFilterSpecified() {
        MomentFilterDto filters = MomentFilterDto.builder()
                .date(NOW)
                .build();
        boolean isAplicable = momentDataFilter.isApplicable(filters);
        assertTrue(isAplicable);
    }

    @Test
    public void testShouldReturnFalseIsFilterNotSpecified() {
        MomentFilterDto filters = MomentFilterDto.builder().build();
        boolean isAplicable = momentDataFilter.isApplicable(filters);
        assertFalse(isAplicable);
    }

    @Test
    public void testShouldReturnFilteredProjectList() {
        MomentFilterDto filters = MomentFilterDto.builder()
                .date(NOW)
                .build();
        Stream<Moment> receivedProjects = momentDataFilter.apply(() -> MOMENTS.stream(), filters);
        assertEquals(FILTERED_MOMENTS, receivedProjects.toList());
    }
}