package faang.school.projectservice.util;

import faang.school.projectservice.dto.moment.MomentDto;

import java.util.List;

import static faang.school.projectservice.util.TestUser.USER_ID_1;

public class TestMomentDto {

    public static long MOMENT_DTO_ID_1 = 1L;
    public static long MOMENT_DTO_ID_2 = 2L;
    public static MomentDto MOMENT_DTO = MomentDto.builder()
            .projectsId(List.of(MOMENT_DTO_ID_1))
            .usersId(List.of(USER_ID_1))
            .build();

    public static MomentDto MOMENT_DTO_EMPTY_PROJECT = MomentDto.builder()
            .build();

    public static MomentDto MOMENT_DTO_MANY_PROJECT = MomentDto.builder()
            .projectsId(List.of(MOMENT_DTO_ID_1, MOMENT_DTO_ID_2))
            .build();
}