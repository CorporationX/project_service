package faang.school.projectservice.filter.meet;

import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.model.meet.Meet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class MeetStartFilterTest {

    private MeetStartFilter meetStartFilter;

    @BeforeEach
    public void setUp() {
        meetStartFilter = new MeetStartFilter();
    }

    @Test
    @DisplayName("testing isApplicable with non appropriate value")
    void testIsApplicableWithNonAppropriateValue() {
        MeetFilterDto meetFilterDto = new MeetFilterDto();
        assertFalse(meetStartFilter.isApplicable(meetFilterDto));
    }

    @Test
    @DisplayName("testing isApplicable with appropriate value")
    void testIsApplicableWithAppropriateValue() {
        MeetFilterDto meetFilterDto = MeetFilterDto.builder()
                .startDate(LocalDateTime.now()).build();
        assertTrue(meetStartFilter.isApplicable(meetFilterDto));
    }

    @Test
    @DisplayName("testing filter with non appropriate value")
    void testFilterNegative() {
        Meet meet = Meet.builder()
                .startDate(LocalDateTime.now().minusDays(1)).build();
        MeetFilterDto meetFilterDto = MeetFilterDto.builder()
                .startDate(LocalDateTime.now()).build();
        List<Meet> meetList = meetStartFilter.filter(Stream.of(meet), meetFilterDto).toList();
        assertEquals(0, meetList.size());
    }

    @Test
    @DisplayName("testing filter with appropriate value")
    void testFilterPositive() {
        Meet meet = Meet.builder()
                .startDate(LocalDateTime.now().plusDays(1)).build();
        MeetFilterDto meetFilterDto = MeetFilterDto.builder()
                .startDate(LocalDateTime.now()).build();
        List<Meet> meetList = meetStartFilter.filter(Stream.of(meet), meetFilterDto).toList();
        assertEquals(1, meetList.size());
        assertEquals(meet, meetList.get(0));
    }
}