package faang.school.projectservice.service.moment.filters;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import org.assertj.core.util.VisibleForTesting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static reactor.core.publisher.Mono.when;

public class MonthFilterTest {

    MomentFilterDto momentFilterDto;
    Moment moment1;
    Moment moment2;
    Stream<Moment> momentStream = Stream.of(moment1,moment2);

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
    public void applyTest(){
        MonthFilter monthFilter = new MonthFilter();
        List<Moment> returned = monthFilter.apply(momentStream,momentFilterDto).toList();
        //проверки на длинну листа, контеин нужный момент , отфильтрованный.


    }
}
