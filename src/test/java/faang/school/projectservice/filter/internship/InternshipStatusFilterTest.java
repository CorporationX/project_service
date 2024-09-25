package faang.school.projectservice.filter.internship;

import faang.school.projectservice.dto.filter.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class InternshipStatusFilterTest {

    @InjectMocks
    private InternshipStatusFilter internshipStatusFilter;
    private InternshipFilterDto internshipFilterDto;
    private static final InternshipStatus STATUS_IN_PROGRESS = InternshipStatus.IN_PROGRESS;
    private static final InternshipStatus STATUS_COMPLETED = InternshipStatus.COMPLETED;

    @Nested
    class PositiveTests {
        @Test
        @DisplayName("When filter status field not null return true")
        public void whenInternshipFilterDtoStatusIsNotNullThenReturnTrue() {
            internshipFilterDto = InternshipFilterDto.builder()
                    .status(STATUS_IN_PROGRESS)
                    .build();

            assertTrue(internshipStatusFilter.isApplicable(internshipFilterDto));
        }

        @Test
        @DisplayName("When filter status field is valid return filtered list")
        public void whenInternshipFilterDtoStatusIsValidThenReturnFilteredDtoList() {
            Stream<Internship> internships = Stream.of(Internship.builder()
                            .status(STATUS_IN_PROGRESS)
                            .build(),
                    Internship.builder()
                            .status(STATUS_COMPLETED)
                            .build());

            internshipFilterDto = InternshipFilterDto.builder()
                    .status(STATUS_COMPLETED)
                    .build();

            List<Internship> filteredInternships = List.of(Internship.builder()
                    .status(InternshipStatus.COMPLETED)
                    .build());

            assertEquals(filteredInternships, internshipStatusFilter
                    .applyFilter(internships, internshipFilterDto).toList());
        }

        @Test
        @DisplayName("When filter status is null return false")
        public void whenInternshipFilterDtoStatusIsNullThenReturnFalse() {
            internshipFilterDto = InternshipFilterDto.builder()
                    .status(null)
                    .build();
            assertFalse(internshipStatusFilter.isApplicable(internshipFilterDto));
        }
    }
}
