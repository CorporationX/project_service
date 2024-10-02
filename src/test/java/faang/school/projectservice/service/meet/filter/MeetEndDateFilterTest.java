package faang.school.projectservice.service.meet.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
class MeetEndDateFilterTest {
    @InjectMocks
    MeetEndDateFilter meetEndDateFilter;

    private SharedData sharedData;

    @BeforeEach
    void init() {
        sharedData = new SharedData();
    }

    @Test
    void testIsApplicable() {
        var result = meetEndDateFilter.isApplicable(sharedData.getMeetFilters());
        assertTrue(result);
    }

    @Test
    void testApply() {
        var resultStream = meetEndDateFilter.apply(sharedData.getMeets().stream(), sharedData.getMeetFilters());
        var resultList = resultStream.toList();
        assertEquals(2, resultList.size());
        assertEquals(resultList.get((0)).getId(), sharedData.getFirstMeet().getId());
        assertEquals(resultList.get((1)).getId(), sharedData.getSecondMeet().getId());
    }
}