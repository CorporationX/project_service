package faang.school.projectservice.filter.meet;

import faang.school.projectservice.model.dto.meet.MeetFilterDto;
import faang.school.projectservice.model.entity.meet.Meet;
import faang.school.projectservice.model.filter.meet.MeetUpdatedAtFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class MeetUpdatedAtFilterTest {

    private MeetUpdatedAtFilter meetUpdatedAtFilter;

    private MeetFilterDto filter;
    private boolean result;
    private LocalDateTime filterDate;
    private Meet meet1;
    private Meet meet2;
    private Stream<Meet> meets;
    private List<Meet> filteredMeets;

    @BeforeEach
    void setUp() {
        meetUpdatedAtFilter = new MeetUpdatedAtFilter();
    }

    @Test
    void isApplicable_shouldReturnTrue_whenUpdatedAtIsNotNull() {
        // given
        filter = MeetFilterDto.builder()
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();
        // when
        result = meetUpdatedAtFilter.isApplicable(filter);
        // then
        assertThat(result).isTrue();
    }

    @Test
    void isApplicable_shouldReturnFalse_whenUpdatedAtIsNull() {
        // given
        filter = MeetFilterDto.builder()
                .updatedAt(null)
                .build();
        // when
        result = meetUpdatedAtFilter.isApplicable(filter);
        // then
        assertThat(result).isFalse();
    }

    @Test
    void apply_shouldFilterMeetsByUpdatedAt() {
        // given
        filterDate = LocalDateTime.now().minusDays(2);
        meet1 = Meet.builder().updatedAt(LocalDateTime.now().minusDays(1)).build();
        meet2 = Meet.builder().updatedAt(LocalDateTime.now().minusDays(3)).build();
        var meet3 = Meet.builder().updatedAt(LocalDateTime.now()).build();
        meets = Stream.of(meet1, meet2, meet3);
        filter = MeetFilterDto.builder()
                .updatedAt(filterDate)
                .build();
        // when
        filteredMeets = meetUpdatedAtFilter.apply(meets, filter).toList();
        // then
        assertThat(filteredMeets).hasSize(1);
        assertThat(filteredMeets).contains(meet2);
        assertThat(filteredMeets).doesNotContain(meet1, meet3);
    }

    @Test
    void apply_shouldReturnEmpty_whenNoMeetsMatchUpdatedAtFilter() {
        // given
        filterDate = LocalDateTime.now().minusDays(5);
        meet1 = Meet.builder().updatedAt(LocalDateTime.now().minusDays(1)).build();
        meet2 = Meet.builder().updatedAt(LocalDateTime.now().minusDays(3)).build();
        meets = Stream.of(meet1, meet2);
        filter = MeetFilterDto.builder()
                .updatedAt(filterDate)
                .build();
        // when
        filteredMeets = meetUpdatedAtFilter.apply(meets, filter).toList();
        // then
        assertThat(filteredMeets).isEmpty();
    }
}