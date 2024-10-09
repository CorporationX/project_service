package faang.school.projectservice.filter.meet;

import faang.school.projectservice.model.dto.meet.MeetFilterDto;
import faang.school.projectservice.model.entity.meet.Meet;
import faang.school.projectservice.model.filter.meet.MeetCreatedAtFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class MeetCreatedAtFilterTest {

    private MeetCreatedAtFilter meetCreatedAtFilter;

    private MeetFilterDto filter;
    private Meet meet1;
    private Meet meet2;
    private LocalDateTime filterDate;
    private boolean result;
    private Stream<Meet> meets;
    private List<Meet> filteredMeets;

    @BeforeEach
    void setUp() {
        meetCreatedAtFilter = new MeetCreatedAtFilter();
    }

    @Test
    void isApplicable_whenCreatedAtIsNotNull_shouldReturnTrue() {
        // given
        filter = MeetFilterDto.builder()
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();
        // when
        result = meetCreatedAtFilter.isApplicable(filter);
        // then
        assertThat(result).isTrue();
    }

    @Test
    void isApplicable_whenCreatedAtIsNull_shouldReturnFalse() {
        // given
        filter = MeetFilterDto.builder()
                .createdAt(null)
                .build();
        // when
        result = meetCreatedAtFilter.isApplicable(filter);
        // then
        assertThat(result).isFalse();
    }

    @Test
    void apply_shouldFilterMeetsByCreatedAt() {
        // given
        filterDate = LocalDateTime.now().minusDays(2);
        meet1 = Meet.builder().createdAt(LocalDateTime.now().minusDays(1)).build();
        meet2 = Meet.builder().createdAt(LocalDateTime.now().minusDays(3)).build();
        var meet3 = Meet.builder().createdAt(LocalDateTime.now()).build();
        meets = Stream.of(meet1, meet2, meet3);
        filter = MeetFilterDto.builder()
                .createdAt(filterDate)
                .build();
        // when
        filteredMeets = meetCreatedAtFilter.apply(meets, filter).toList();
        // then
        assertThat(filteredMeets).hasSize(2);
        assertThat(filteredMeets).contains(meet1, meet3);
        assertThat(filteredMeets).doesNotContain(meet2);
    }

    @Test
    void apply_whenNoMeetsMatchCreatedAtFilter_shouldReturnEmpty() {
        // given
        filterDate = LocalDateTime.now();
        meet1 = Meet.builder().createdAt(LocalDateTime.now().minusDays(2)).build();
        meet2 = Meet.builder().createdAt(LocalDateTime.now().minusDays(3)).build();
        meets = Stream.of(meet1, meet2);
        filter = MeetFilterDto.builder()
                .createdAt(filterDate)
                .build();
        // when
        filteredMeets = meetCreatedAtFilter.apply(meets, filter).toList();
        // then
        assertThat(filteredMeets).isEmpty();
    }
}