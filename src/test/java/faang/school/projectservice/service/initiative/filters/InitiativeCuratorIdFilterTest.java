package faang.school.projectservice.service.initiative.filters;

import faang.school.projectservice.dto.initiative.InitiativeFilterDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.initiative.Initiative;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class InitiativeCuratorIdFilterTest {
    @Spy
    private InitiativeCuratorIdFilter curatorIdFilter;

    private InitiativeFilterDto filterDto;
    private Initiative initiative1, initiative2, initiative3;

    @BeforeEach
    void init() {
        filterDto = InitiativeFilterDto.builder()
                .curatorId(1L)
                .build();

        initiative1 = Initiative.builder()
                .id(1L)
                .curator(TeamMember.builder().userId(1L).build())
                .build();

        initiative2 = Initiative.builder()
                .id(2L)
                .curator(TeamMember.builder().userId(2L).build())
                .build();

        initiative3 = Initiative.builder()
                .id(3L)
                .curator(TeamMember.builder().userId(1L).build())
                .build();
    }

    @Test
    void isAcceptableBadDto() {
        filterDto.setCuratorId(null);
        assertFalse(curatorIdFilter.isAcceptable(filterDto));
    }

    @Test
    void isAcceptableGoodDto() {
        assertTrue(curatorIdFilter.isAcceptable(filterDto));
    }

    @Test
    void apply() {
        Initiative[] expected = new Initiative[]{initiative1, initiative3};
        Stream<Initiative> out = curatorIdFilter.apply(Stream.of(initiative1, initiative2, initiative3), filterDto);
        assertArrayEquals(expected, out.toArray());
    }
}