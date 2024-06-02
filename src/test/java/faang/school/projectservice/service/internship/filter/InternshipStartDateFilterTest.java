package faang.school.projectservice.service.internship.filter;

import faang.school.projectservice.dto.filter.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.testData.internship.InternshipTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InternshipStartDateFilterTest {
    private final InternshipStartDateFilter internshipStartDateFilter = new InternshipStartDateFilter();
    private InternshipFilterDto filter;
    private List<Internship> allInternships;

    @BeforeEach
    void setUp() {
        InternshipTestData internshipTestData = new InternshipTestData();
        allInternships = List.of(internshipTestData.getInternship());
        allInternships.forEach(internship -> internship.setInterns(List.of()));

        filter = new InternshipFilterDto();
        filter.setStartDatePattern(LocalDateTime.of(2024, 1, 30, 16, 0));
    }

    @Nested
    class PositiveTests {
        @DisplayName("should return true when in filterDto startDateFilter isn't null")
        @Test
        void isApplicableTest() {
            var isApplicable = internshipStartDateFilter.isApplicable(filter);

            assertTrue(isApplicable);
        }

        @DisplayName("should return allInternships when all of them have startDate after or equals of startDatePattern")
        @Test
        void applyTest() {
            var filteredInternships = internshipStartDateFilter.apply(allInternships, filter).toList();

            assertEquals(allInternships, filteredInternships);
        }
    }

    @Nested
    class NegativeTests {
        @DisplayName("should return false when in filterDto startDataPattern is null")
        @Test
        void isApplicableTest() {
            filter.setStartDatePattern(null);
            var isApplicable = internshipStartDateFilter.isApplicable(filter);

            assertFalse(isApplicable);
        }

        @DisplayName("should return empty list when all of them have startDate af startDatePattern")
        @Test
        void applyTest() {
            filter.setStartDatePattern(LocalDateTime.of(2024, 10, 30, 16, 0));
            var filteredInternships = internshipStartDateFilter.apply(allInternships, filter).toList();

            assertEquals(List.of(), filteredInternships);
        }
    }
}