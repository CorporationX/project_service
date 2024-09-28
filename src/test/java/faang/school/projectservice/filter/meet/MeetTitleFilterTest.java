package faang.school.projectservice.filter.meet;

import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.model.meet.Meet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class MeetTitleFilterTest {

    private MeetTitleFilter meetTitleFilter;

    private MeetFilterDto filter;
    private boolean result;
    private Meet meet1;
    private Meet meet2;
    private Meet meet3;
    private Stream<Meet> meets;
    private List<Meet> filteredMeets;

    @BeforeEach
    void setUp() {
        meetTitleFilter = new MeetTitleFilter();
    }

    @Test
    void isApplicable_whenTitlePatternIsNotNull_shouldReturnTrue() {
        // given
        filter = MeetFilterDto.builder().titlePattern("meeting").build();
        // when
        result = meetTitleFilter.isApplicable(filter);
        // then
        assertThat(result).isTrue();
    }

    @Test
    void isApplicable_whenTitlePatternIsNull_shouldReturnFalse() {
        // given
        filter = MeetFilterDto.builder().titlePattern(null).build();
        // when
        result = meetTitleFilter.isApplicable(filter);
        // then
        assertThat(result).isFalse();
    }

    @Test
    void apply_shouldFilterMeetsByTitle() {
        // given
        meet1 = Meet.builder().title("Team Meeting").build();
        meet2 = Meet.builder().title("Project Discussion").build();
        meet3 = Meet.builder().title("Team Standup").build();
        meets = Stream.of(meet1, meet2, meet3);
        filter = MeetFilterDto.builder().titlePattern("team").build();
        // when
        filteredMeets = meetTitleFilter.apply(meets, filter).toList();
        // then
        assertThat(filteredMeets).hasSize(2);
        assertThat(filteredMeets).contains(meet1, meet3);
        assertThat(filteredMeets).doesNotContain(meet2);
    }

    @Test
    void apply_whenNoTitlesMatch_shouldReturnEmpty() {
        // given
        meet1 = Meet.builder().title("Team Meeting").build();
        meet2 = Meet.builder().title("Project Discussion").build();
        meet3 = Meet.builder().title("Team Standup").build();
        meets = Stream.of(meet1, meet2, meet3);
        filter = MeetFilterDto.builder().titlePattern("workshop").build();
        // when
        filteredMeets = meetTitleFilter.apply(meets, filter).toList();
        // then
        assertThat(filteredMeets).isEmpty();
    }
}