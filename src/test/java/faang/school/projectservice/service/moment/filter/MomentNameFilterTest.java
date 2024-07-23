package faang.school.projectservice.service.moment.filter;

import faang.school.projectservice.dto.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MomentNameFilterTest {

    private final MomentNameFilter momentNameFilter = new MomentNameFilter();
    private Stream<Moment> momentsStream;

    @BeforeEach
    void initFilter() {
        momentsStream = TestDataFactory.createMomentList().stream();
    }

    @Test
    void shouldReturnTrueIfFilterSpecified() {
        // given - precondition
        var momentFilterDto = TestDataFactory.createMomentFilterDto();

        // when - action
        var isApplicable = momentNameFilter.isApplicable(momentFilterDto);

        // then - verify the output
        assertThat(isApplicable).isTrue();
    }

    @Test
    void shouldReturnFalseIfFilterIsNotSpecified() {
        // given - precondition
        var momentFilterDto = new MomentFilterDto();

        // when - action
        var isApplicable = momentNameFilter.isApplicable(momentFilterDto);

        // then - verify the output
        assertThat(isApplicable).isFalse();
    }


    @Test
    void shouldReturnFilteredMomentList() {
        // given - precondition
        var momentFilterDto = TestDataFactory.createMomentFilterDto();

        // when - action
        var applicableFilters = momentNameFilter.getApplicableFilters(momentsStream, momentFilterDto);

        // then - verify the output
        assertThat(applicableFilters.map(Moment::getName).collect(Collectors.joining(" ")))
                .isEqualTo(momentFilterDto.getNamePattern());
    }
}