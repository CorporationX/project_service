package faang.school.projectservice.filter.meet;

import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.model.meet.Meet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class MeetTitleFilterTest {

    private MeetTitleFilter meetTitleFilter;

    @BeforeEach
    public void setUp() {
        meetTitleFilter = new MeetTitleFilter();
    }

    @Test
    @DisplayName("testing isApplicable with non appropriate value")
    void testIsApplicableWithNonAppropriateValue() {
        MeetFilterDto meetFilterDto = new MeetFilterDto();
        assertFalse(meetTitleFilter.isApplicable(meetFilterDto));
    }

    @Test
    @DisplayName("testing isApplicable with appropriate value")
    void testIsApplicableWithAppropriateValue() {
        MeetFilterDto meetFilterDto = MeetFilterDto.builder()
                .titlePattern("title").build();
        assertTrue(meetTitleFilter.isApplicable(meetFilterDto));
    }

    @Test
    @DisplayName("testing filter with non appropriate value")
    void testFilterNegative() {
        Meet meet = Meet.builder()
                .title("Title").build();
        MeetFilterDto meetFilterDto = MeetFilterDto.builder()
                .titlePattern("not").build();
        List<Meet> meetList = meetTitleFilter.filter(Stream.of(meet), meetFilterDto).toList();
        assertEquals(0, meetList.size());
    }

    @Test
    @DisplayName("testing filter with appropriate value")
    void testFilterPositive() {
        Meet meet = Meet.builder()
                .title("Title").build();
        MeetFilterDto meetFilterDto = MeetFilterDto.builder()
                .titlePattern("title").build();
        List<Meet> meetList = meetTitleFilter.filter(Stream.of(meet), meetFilterDto).toList();
        assertEquals(1, meetList.size());
        assertEquals(meet, meetList.get(0));
    }
}