package faang.school.projectservice.service.moment.filter;

import faang.school.projectservice.dto.moment.filter.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MomentDateFilterTest {

    @Test
    public void testGetAllMomentsByDate() {
        MomentFilter momentDateFilter = new MomentDateFilter();
        List<Moment> moments = getMomentDateList();
        List<Moment> expectedList = getExpectedDateList();
        MomentFilterDto momentFilterDto = MomentFilterDto.builder()
                .startDate(LocalDateTime.of(2024, 6, 1, 0, 0))
                .endDate(LocalDateTime.now())
                .build();

        Stream<Moment> momentStream = momentDateFilter.apply(moments, momentFilterDto);

        assertEquals(expectedList, momentStream.toList());
    }

    private static List<Moment> getMomentDateList() {
        return List.of(Moment.builder()
                        .id(1L)
                        .name("Moment1")
                        .date(LocalDateTime.now().plusHours(1))
                        .build(),
                Moment.builder()
                        .id(2L)
                        .name("Moment2")
                        .date(LocalDateTime.of(2024, 6, 1, 12, 0))
                        .build(),
                Moment.builder()
                        .id(3L)
                        .name("Moment3")
                        .date(LocalDateTime.of(2024, 4, 1, 12, 0))
                        .build(),
                Moment.builder()
                        .id(4L)
                        .name("Moment4")
                        .date(LocalDateTime.of(2024, 6, 5, 15, 30))
                        .build()
        );
    }

    private static List<Moment> getExpectedDateList() {
        return List.of(Moment.builder()
                        .id(2L)
                        .name("Moment2")
                        .date(LocalDateTime.of(2024, 6, 1, 12, 0))
                        .build(),
                Moment.builder()
                        .id(4L)
                        .name("Moment4")
                        .date(LocalDateTime.of(2024, 6, 5, 15, 30))
                        .build()
        );
    }
}