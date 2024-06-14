package faang.school.projectservice.util;

import faang.school.projectservice.model.Moment;

import java.time.LocalDateTime;
import java.util.List;

public class TestMoment {
    public static LocalDateTime NOW = LocalDateTime.now();

    public static long MOMENT_ID_1 = 1L;
    public static long MOMENT_ID_2 = 2L;
    public static long MOMENT_ID_3 = 3L;

    public static Moment MOMENT_1 = Moment.builder().id(MOMENT_ID_1).date(NOW).build();
    public static Moment MOMENT_2 = Moment.builder().id(MOMENT_ID_2).date(NOW.minusMonths(1)).build();
    public static Moment MOMENT_3 = Moment.builder().id(MOMENT_ID_3).date(NOW.plusMonths(1)).build();

    public static List<Moment> MOMENTS = List.of(MOMENT_1, MOMENT_2, MOMENT_3);
    public static List<Moment> FILTERED_MOMENTS = List.of(MOMENT_1);
}

