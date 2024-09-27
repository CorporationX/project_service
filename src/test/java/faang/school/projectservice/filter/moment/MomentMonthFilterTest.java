package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MomentMonthFilterTest {
    private MomentMonthFilter momentMonthFilter;

    private MomentFilterDto filterDto;
    private Moment moment;

    @BeforeEach
    void setUp() {
        filterDto = MomentFilterDto.builder().build();
        momentMonthFilter = new MomentMonthFilter();
        moment = new Moment();
        moment.setDate(LocalDateTime.of(2024, 9, 21, 10, 10));
    }

    @Test
    void givenNullWhenIsApplicableThenReturnFalse() {
        filterDto = MomentFilterDto.builder()
                .month(0)
                .build();

        var result = momentMonthFilter.isApplicable(filterDto);

        assertFalse(result);
    }

    @Test
    void givenValidWhenIsApplicableThenReturnTrue() {
        filterDto = MomentFilterDto.builder()
                .month(1)
                .build();

        var result = momentMonthFilter.isApplicable(filterDto);

        assertTrue(result);
    }

    @Test
    void givenValidWhenApplyThenReturnMoment() {
        filterDto = MomentFilterDto.builder()
                .month(9)
                .build();

        var result = momentMonthFilter.apply(Stream.of(moment), filterDto);

        assertEquals(moment, result.findFirst().get());
    }

    @Test
    void givenNotValidWhenApplyThenReturnEmpty() {
        filterDto = MomentFilterDto.builder()
                .month(2)
                .build();

        var result = momentMonthFilter.apply(Stream.of(moment), filterDto);

        assertEquals(Optional.empty(), result.findFirst());
    }
}