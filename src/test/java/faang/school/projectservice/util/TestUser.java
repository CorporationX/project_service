package faang.school.projectservice.util;

import java.util.List;

public class TestUser {
    public static long USER_ID_1 = 1L;
    public static long USER_ID_2 = 2L;
    public static long USER_ID_3 = 3L;
    public static long USER_ID_4 = 4L;

    public static List<Long> USERS_ID = List.of(USER_ID_3, USER_ID_4);
    public static List<Long> EXPECTED_USERS_IDS = List.of(USER_ID_1, USER_ID_2,USER_ID_3, USER_ID_4);
}
