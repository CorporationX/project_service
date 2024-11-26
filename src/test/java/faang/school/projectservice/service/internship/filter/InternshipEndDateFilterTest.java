package faang.school.projectservice.service.internship.filter;

import faang.school.projectservice.dto.internship.filter.InternshipFilterDto;
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

class InternshipEndDateFilterTest {
    private final InternshipEndDateFilter internshipEndDateFilter = new InternshipEndDateFilter();
    private InternshipFilterDto filter;
    private List<Internship> allInternships;

    @BeforeEach
    void setUp() {
        InternshipTestData internshipTestData = new InternshipTestData();
        allInternships = List.of(internshipTestData.getInternship());
        allInternships.forEach(internship -> internship.setInterns(List.of()));

        filter = new InternshipFilterDto();
        filter.setEndDatePattern(LocalDateTime.of(3024, 9, 30, 16, 0));
    }

    @Nested
    class PositiveTests {
        @DisplayName("should return true when in filterDto endDateFilter isn't null")
        @Test
        void isApplicableTest() {
            var isApplicable = internshipEndDateFilter.isApplicable(filter);

            assertTrue(isApplicable);
        }

        @DisplayName("should return allInternships when all of them have endDate before or equals of endDatePattern")
        @Test
        void applyTest() {
            var filteredInternships = internshipEndDateFilter.apply(allInternships, filter).toList();

            assertEquals(allInternships, filteredInternships);
        }
    }

    @Nested
    class NegativeTests {
        @DisplayName("should return false when in filterDto endDataPattern is null")
        @Test
        void isApplicableTest() {
            filter.setEndDatePattern(null);
            var isApplicable = internshipEndDateFilter.isApplicable(filter);

            assertFalse(isApplicable);
        }

        @DisplayName("should return empty list when all of them have endDate later than endDatePattern")
        @Test
        void applyTest() {
            filter.setEndDatePattern(LocalDateTime.of(2024, 6, 30, 16, 0));
            var filteredInternships = internshipEndDateFilter.apply(allInternships, filter).toList();

            assertEquals(List.of(), filteredInternships);
        }
    }
}