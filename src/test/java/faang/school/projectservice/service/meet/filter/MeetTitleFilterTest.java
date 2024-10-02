package faang.school.projectservice.service.meet.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class MeetTitleFilterTest {
    @InjectMocks
    MeetTitleFilter meetTitleFilter;
    private SharedData sharedData;

    @BeforeEach
    void init() {
        sharedData = new SharedData();
    }

    @Test
    void testIsApplicable() {
        var result = meetTitleFilter.isApplicable(sharedData.getMeetFilters());
        assertTrue(result);
    }

    @Test
    void testApply() {
        var resultStream = meetTitleFilter.apply(sharedData.getMeets().stream(), sharedData.getMeetFilters());
        var resultList = resultStream.toList();
        var meet = resultList.get(0);
        assertEquals(1, resultList.size());
        assertEquals(sharedData.getFirstMeet().getId(), meet.getId());
        assertEquals(sharedData.getFirstMeet().getTitle(), meet.getTitle());
    }
}