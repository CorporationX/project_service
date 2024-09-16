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
        filterDto = new MomentFilterDto();
        momentMonthFilter = new MomentMonthFilter();
        moment = new Moment();
        moment.setDate(LocalDateTime.of(2024, 9, 21, 10, 10));
    }

    @Test
    void givenNullWhenIsApplicableThenReturnFalse() {
        filterDto.setMonth(0);

        var result = momentMonthFilter.isApplicable(filterDto);

        assertFalse(result);
    }

    @Test
    void givenValidWhenIsApplicableThenReturnTrue() {
        filterDto.setMonth(1);

        var result = momentMonthFilter.isApplicable(filterDto);

        assertTrue(result);
    }

    @Test
    void givenValidWhenApplyThenReturnMoment() {
        filterDto.setMonth(9);

        var result = momentMonthFilter.apply(Stream.of(moment), filterDto);

        assertEquals(moment, result.findFirst().get());
    }

    @Test
    void givenNotValidWhenApplyThenReturnEmpty() {
        filterDto.setMonth(2);

        var result = momentMonthFilter.apply(Stream.of(moment), filterDto);

        assertEquals(Optional.empty(), result.findFirst());
    }
}