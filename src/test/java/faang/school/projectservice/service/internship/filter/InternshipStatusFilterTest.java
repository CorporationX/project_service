package faang.school.projectservice.service.internship.filter;

import faang.school.projectservice.dto.filter.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.testData.internship.InternshipTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static faang.school.projectservice.model.InternshipStatus.COMPLETED;
import static faang.school.projectservice.model.InternshipStatus.IN_PROGRESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InternshipStatusFilterTest {
    private final InternshipStatusFilter internshipStatusFilter = new InternshipStatusFilter();
    private InternshipFilterDto filter;
    private List<Internship> allInternships;

    @BeforeEach
    void setUp() {
        InternshipTestData internshipTestData = new InternshipTestData();
        allInternships = List.of(internshipTestData.getInternship());
        allInternships.forEach(internship -> internship.setInterns(List.of()));

        filter = new InternshipFilterDto();
        filter.setStatusPattern(IN_PROGRESS);
    }

    @Nested
    class PositiveTests {
        @DisplayName("should return true when in filterDto statusPattern isn't null")
        @Test
        void isApplicableTest() {
            var isApplicable = internshipStatusFilter.isApplicable(filter);

            assertTrue(isApplicable);
        }

        @DisplayName("should return allInternships when all of them have status equal to statusPattern")
        @Test
        void applyTest() {
            var filteredInternships = internshipStatusFilter.apply(allInternships, filter).toList();

            assertEquals(allInternships, filteredInternships);
        }
    }

    @Nested
    class NegativeTests {
        @DisplayName("should return false when in filterDto statusPattern is null")
        @Test
        void isApplicableTest() {
            filter.setStatusPattern(null);
            var isApplicable = internshipStatusFilter.isApplicable(filter);

            assertFalse(isApplicable);
        }

        @DisplayName("should return empty list when all of them haven't status equal to endDatePattern")
        @Test
        void applyTest() {
            filter.setStatusPattern(COMPLETED);
            var filteredInternships = internshipStatusFilter.apply(allInternships, filter).toList();

            assertEquals(List.of(), filteredInternships);
        }
    }
}