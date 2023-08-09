package faang.school.projectservice.service.moment.filter;

import faang.school.projectservice.dto.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MomentDateFilterTest {
    private MomentDateFilter momentDateFilter;
    List<Moment> moments;

    @BeforeEach
    void init() {
        momentDateFilter = new MomentDateFilter();
        moments = List.of(Moment.builder()
                        .id(1L)
                        .name("Moment 1")
                        .date(LocalDateTime.now())
                        .build(),
                Moment.builder()
                        .id(2L)
                        .name("Moment 2")
                        .date(LocalDateTime.of(2023, 7, 1, 12, 0))
                        .build(),
                Moment.builder()
                        .id(3L)
                        .name("Moment 3")
                        .date(LocalDateTime.of(2023, 4, 1, 12, 0))
                        .build(),
                Moment.builder()
                        .id(4L)
                        .name("Moment 4")
                        .date(LocalDateTime.of(2023, 7, 5, 15, 30))
                        .build()
        );
    }

    @Test
    public void testIsApplicableDatePatternShouldReturnTrue() {
        MomentFilterDto momentFilterDto = MomentFilterDto.builder()
                .datePattern(LocalDateTime.now())
                .build();
        assertTrue(momentDateFilter.isApplicable(momentFilterDto));
    }

    @Test
    public void testIsApplicableDatePatternShouldReturnFalse() {
        MomentFilterDto momentFilterDto = MomentFilterDto.builder().build();
        assertFalse(momentDateFilter.isApplicable(momentFilterDto));
    }

    @Test
    public void testApplyDatePatternShouldReturnFilteredList() {
        List<Moment> expectedList = List.of(Moment.builder()
                        .id(2L)
                        .name("Moment 2")
                        .date(LocalDateTime.of(2023, 7, 1, 12, 0))
                        .build(),
                Moment.builder()
                        .id(4L)
                        .name("Moment 4")
                        .date(LocalDateTime.of(2023, 7, 5, 15, 30))
                        .build()
        );

        MomentFilterDto momentFilterDto = MomentFilterDto.builder()
                .datePattern(LocalDateTime.of(2023, 7, 1, 0, 0))
                .build();

        Stream<Moment> momentStream = momentDateFilter.apply(moments.stream(), momentFilterDto);
        assertEquals(expectedList, momentStream.toList());
    }
}
