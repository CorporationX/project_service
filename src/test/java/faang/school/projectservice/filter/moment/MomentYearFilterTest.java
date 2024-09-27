package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MomentYearFilterTest {
    private MomentYearFilter momentYearFilter;

    private MomentFilterDto filterDto;
    private Moment moment;

    @BeforeEach
    void setUp() {
        momentYearFilter = new MomentYearFilter();
        filterDto = MomentFilterDto.builder().build();
        moment = new Moment();
        moment.setDate(LocalDateTime.of(2024, 9, 21, 10, 10));
    }

    @Test
    void givenValidWhenIsApplicableThenTrue() {
        filterDto = MomentFilterDto.builder()
                .year(2024)
                .build();

        var result = momentYearFilter.isApplicable(filterDto);

        assertTrue(result);
    }

    @Test
    void givenNotValidWhenIsApplicableThenFalse() {

        var result = momentYearFilter.isApplicable(filterDto);

        assertFalse(result);
    }

    @Test
    void givenValidWhenApplyThenReturnMoment() {
        filterDto = MomentFilterDto.builder()
                .year(2024)
                .build();

        var result = momentYearFilter.apply(Stream.of(moment), filterDto);

        assertEquals(moment, result.findFirst().get());
    }

    @Test
    void givenNotValidWhenApplyThenReturnEmpty() {
        filterDto = MomentFilterDto.builder()
                .year(2023)
                .build();

        var result = momentYearFilter.apply(Stream.of(moment), filterDto);

        assertEquals(Optional.empty(), result.findFirst());
    }
}