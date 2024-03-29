package faang.school.projectservice.service.moment.filters;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MonthFilterTest {

    private MomentFilterDto momentFilterDto;
    private Moment moment1;
    private Moment moment2;

    @BeforeEach
    void init() {
        momentFilterDto = MomentFilterDto.builder()
                .month(1)
                .build();
        moment1 = Moment.builder()
                .date(LocalDateTime.of(2024, 1, 15, 10, 30))
                .build();
        moment2 = Moment.builder()
                .date(LocalDateTime.of(2024, 2, 15, 10, 30))
                .build();
    }


    @Test
    public void isApplicableTestTrue() {
        MonthFilter monthFilter = new MonthFilter();
        assertTrue(monthFilter.isApplicable(momentFilterDto));
    }

    @Test
    public void isApplicableTestFalse() {
        momentFilterDto.setMonth(null);
        MonthFilter monthFilter = new MonthFilter();
        assertFalse(monthFilter.isApplicable(momentFilterDto));
    }

    @Test
    public void applyTest() {
        Stream<Moment> momentStream = Stream.of(moment1, moment2);
        MonthFilter monthFilter = new MonthFilter();
        String targetMonth = "JANUARY";

        List<Moment> returnedList = monthFilter.apply(momentStream, momentFilterDto).toList();

        assertEquals(returnedList.size(), 1);
        assertEquals(returnedList.get(0).getDate().getMonth(), moment1.getDate().getMonth());
        assertFalse(returnedList.contains(targetMonth));
    }
}
