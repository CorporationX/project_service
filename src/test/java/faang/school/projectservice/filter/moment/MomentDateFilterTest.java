package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MomentDateFilterTest {

    private static final LocalDateTime START_DATE = LocalDateTime.of(2024, 9, 1, 0, 0);
    private static final LocalDateTime END_DATE = LocalDateTime.of(2024, 9, 10, 0, 0);
    private static final LocalDateTime MOMENT_DATE_1 = LocalDateTime.of(2024, 9, 1, 10, 0);
    private static final LocalDateTime MOMENT_DATE_2 = LocalDateTime.of(2024, 9, 5, 15, 0);
    private static final LocalDateTime MOMENT_DATE_3 = LocalDateTime.of(2024, 9, 10, 20, 0);
    private static final String MOMENT1_NAME = "Moment1";
    private static final String MOMENT2_NAME = "Moment2";
    private static final String MOMENT3_NAME = "Moment3";
    private static final Long ID_1 = 1L;
    private static final Long ID_2 = 2L;
    private static final Long ID_3 = 3L;

    @InjectMocks
    private MomentDateFilter momentDateFilter;

    private MomentFilterDto momentFilterDto;
    private List<Moment> momentList;

    @BeforeEach
    void setUp() {
        momentFilterDto = new MomentFilterDto();

        Moment moment1 = new Moment();
        moment1.setId(ID_1);
        moment1.setName(MOMENT1_NAME);
        moment1.setDate(MOMENT_DATE_1);

        Moment moment2 = new Moment();
        moment2.setId(ID_2);
        moment2.setName(MOMENT2_NAME);
        moment2.setDate(MOMENT_DATE_2);

        Moment moment3 = new Moment();
        moment3.setId(ID_3);
        moment3.setName(MOMENT3_NAME);
        moment3.setDate(MOMENT_DATE_3);

        momentList = List.of(moment1, moment2, moment3);
    }

    @Nested
    @DisplayName("Test isApplicable method")
    class IsApplicableTests {

        @Test
        @DisplayName("When start and end dates are provided then filter should be applicable")
        void whenStartAndEndDateProvidedThenFilterIsApplicable() {
            momentFilterDto.setStartDate(START_DATE);
            momentFilterDto.setEndDate(END_DATE);

            assertTrue(momentDateFilter.isApplicable(momentFilterDto), "Filter should be applicable with start and end date");
        }

        @Test
        @DisplayName("When no dates are provided then filter should not be applicable")
        void whenNoDatesProvidedThenFilterIsNotApplicable() {
            assertFalse(momentDateFilter.isApplicable(momentFilterDto), "Filter should not be applicable when no dates are provided");
        }
    }

    @Nested
    @DisplayName("Test apply method")
    class ApplyMethodTests {

        @Test
        @DisplayName("When start and end dates are provided then filter moments by date range")
        void whenStartAndEndDateProvidedThenFilterByDateRange() {
            momentFilterDto.setStartDate(START_DATE);
            momentFilterDto.setEndDate(END_DATE);

            List<Moment> filteredMoments = momentDateFilter
                    .apply(momentList.stream(), momentFilterDto)
                    .toList();

            assertEquals(2, filteredMoments.size(), "The filtered moments should contain 2 elements");
            assertEquals(MOMENT_DATE_1, filteredMoments.get(0).getDate());
            assertEquals(MOMENT_DATE_2, filteredMoments.get(1).getDate());
        }

        @Test
        @DisplayName("When only start date is provided then filter moments from start date onwards")
        void whenOnlyStartDateProvidedThenFilterFromStartDateOnwards() {
            momentFilterDto.setStartDate(LocalDateTime.of(2024, 9, 3, 0, 0));

            List<Moment> filteredMoments = momentDateFilter
                    .apply(momentList.stream(), momentFilterDto)
                    .toList();

            assertEquals(2, filteredMoments.size(), "The filtered moments should contain 2 elements");
            assertEquals(MOMENT_DATE_2, filteredMoments.get(0).getDate());
            assertEquals(MOMENT_DATE_3, filteredMoments.get(1).getDate());
        }

        @Test
        @DisplayName("When only end date is provided then filter moments up to end date")
        void whenOnlyEndDateProvidedThenFilterUpToEndDate() {
            momentFilterDto.setEndDate(LocalDateTime.of(2024, 9, 7, 0, 0));

            List<Moment> filteredMoments = momentDateFilter
                    .apply(momentList.stream(), momentFilterDto)
                    .toList();

            assertEquals(2, filteredMoments.size(), "The filtered moments should contain 2 elements");
            assertEquals(MOMENT_DATE_1, filteredMoments.get(0).getDate());
            assertEquals(MOMENT_DATE_2, filteredMoments.get(1).getDate());
        }
    }
}
