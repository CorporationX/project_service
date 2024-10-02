package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MomentProjectFilterTest {
    private MomentProjectFilter momentProjectFilter;

    private MomentFilterDto filterDto;
    private Moment moment;

    @BeforeEach
    void setUp() {
        momentProjectFilter = new MomentProjectFilter();
        filterDto = MomentFilterDto.builder().build();
        moment = new Moment();
        moment.setProjects(List.of(Project.builder()
                .id(2L)
                .build()));
    }

    @Test
    void givenValidWhenIsApplicableThenReturnTrue() {
        filterDto = MomentFilterDto.builder()
                .projectId(List.of(2L))
                .build();

        var result = momentProjectFilter.isApplicable(filterDto);

        assertEquals(true, result);
    }

    @Test
    void givenEmptyWhenIsApplicableThenReturnFalse() {

        var result = momentProjectFilter.isApplicable(filterDto);

        assertEquals(false, result);
    }

    @Test
    void givenValidWhenApplyThenReturnMoment() {
        filterDto = MomentFilterDto.builder()
                .projectId(List.of(2L))
                .build();

        var result = momentProjectFilter.apply(Stream.of(moment), filterDto);

        assertEquals(moment, result.findFirst().get());
    }

    @Test
    void givenNotValidWhenApplyThenReturnEmpty() {
        filterDto = MomentFilterDto.builder()
                .projectId(List.of(3L))
                .build();

        var result = momentProjectFilter.apply(Stream.of(moment), filterDto);

        assertEquals(Optional.empty(), result.findFirst());
    }
}