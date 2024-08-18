package faang.school.projectservice.filter;

import faang.school.projectservice.dto.client.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class MomentDataFilterTest {
    private MomentDataFilter momentDataFilter;
    private MomentFilterDto dataFilterDto;
    private Moment moment;
    private List<Moment> moments;
    private List<MomentFilter> allFilters;

    @BeforeEach
    void setUp() {
        momentDataFilter = new MomentDataFilter();
        dataFilterDto = new MomentFilterDto(1L, "name", LocalDateTime.of(2000, 1,10,0,0));
        moment = Moment.builder().id(1L).date(LocalDateTime.of(2000, 1,5,5,5)).build();
        moments = List.of(moment);
        allFilters = List.of(momentDataFilter);
    }

    @Test
    public void testIsApplicable() {
        Assertions.assertTrue(momentDataFilter.isApplicable(dataFilterDto));
    }

    @Test
    public void testIsNotApplicable() {
        dataFilterDto.setDate(null);
        Assertions.assertFalse(momentDataFilter.isApplicable(dataFilterDto));
    }

    @Test
    public void testApply() {
        Stream<Moment> filteredMoments = momentDataFilter.apply(moments, dataFilterDto);
        Assertions.assertEquals(1, filteredMoments.count());
    }

    @Test
    public void testWhenApplyReturnsEmptyStream() {
        moment.setDate(LocalDateTime.of(2000, 2,10,0,0));
        Stream<Moment> filteredMoments = momentDataFilter.apply(moments, dataFilterDto);
        Assertions.assertEquals(0, filteredMoments.count());
    }

    @Test
    public void testIfApplicableAndApply() {
        List<Moment> filteredMoments = allFilters.stream().filter(filter -> filter.isApplicable(dataFilterDto))
                .flatMap(filter -> filter.apply(moments, dataFilterDto)).toList();
        Assertions.assertEquals(1, filteredMoments.size());
    }
}
