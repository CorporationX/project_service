package faang.school.projectservice.service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class MeetDateFilterTest {
    @InjectMocks
    MeetDateFilter meetDateFilter;

    private SharedData sharedData;

    @BeforeEach
    void init() {
        sharedData = new SharedData();
    }

    @Test
    void testIsApplicable() {
        var meetFilterDto = sharedData.getMeetFilters();
        assertTrue(meetDateFilter.isApplicable(meetFilterDto));
    }

    @Test
    void testApplyTwoDates() {
        var resultStream = meetDateFilter.apply(sharedData.getMeets().stream(), sharedData.getMeetFilters());
        var resultList = resultStream.toList();
        var meet = resultList.get(0);
        assertEquals(1, resultList.size());
        assertEquals(sharedData.getSecondMeet().getId(), meet.getId());
    }

    @Test
    void testApplyBeginDate() {
        sharedData.getMeetFilters().setEnd(null);
        var resultStream = meetDateFilter.apply(sharedData.getMeets().stream(), sharedData.getMeetFilters());
        var resultList = resultStream.toList();
        var meet1 = resultList.get(0);
        var meet2 = resultList.get(1);

        assertEquals(2, resultList.size());
        assertEquals(sharedData.getSecondMeet().getId(), meet1.getId());
        assertEquals(sharedData.getThirdMeet().getId(), meet2.getId());
    }

    @Test
    void testApplyEndDate() {
        sharedData.getMeetFilters().setBegin(null);
        var resultStream = meetDateFilter.apply(sharedData.getMeets().stream(), sharedData.getMeetFilters());
        var resultList = resultStream.toList();
        var meet1 = resultList.get(0);
        var meet2 = resultList.get(1);

        assertEquals(2, resultList.size());
        assertEquals(sharedData.getFirstMeet().getId(), meet1.getId());
        assertEquals(sharedData.getSecondMeet().getId(), meet2.getId());
    }
}