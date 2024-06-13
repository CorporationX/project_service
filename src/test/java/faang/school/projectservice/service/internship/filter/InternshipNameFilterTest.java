package faang.school.projectservice.service.internship.filter;

import faang.school.projectservice.dto.internship.filter.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.testData.internship.InternshipTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InternshipNameFilterTest {
    private final InternshipNameFilter internshipNameFilter = new InternshipNameFilter();
    private InternshipFilterDto filter;
    private List<Internship> internshipsToFilter;

    @BeforeEach
    void setUp() {
        InternshipTestData internshipTestData = new InternshipTestData();
        internshipsToFilter = List.of(internshipTestData.getInternship());

        filter = new InternshipFilterDto();
        filter.setNamePattern("Faang");
    }

    @Nested
    class positiveTests {
        @DisplayName("should return true when pattern isn't empty")
        @Test
        void shouldReturnTrueWhenPatternIsntEmpty() {
            var isApplicable = internshipNameFilter.isApplicable(filter);

            assertTrue(isApplicable);
        }

        @DisplayName("should return filtered internships")
        @Test
        void shouldReturnFilteredEvents() {
            var actualFilteredUsers = internshipNameFilter.apply(internshipsToFilter, filter);

            assertEquals(internshipsToFilter, actualFilteredUsers.toList());
        }
    }

    @Nested
    class NegativeTests {
        @DisplayName("should return false when empty pattern is passed")
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "\t", "\n"})
        void shouldReturnFalseWhenPatternIsEmpty(String pattern) {
            filter.setNamePattern(pattern);

            var isApplicable = internshipNameFilter.isApplicable(filter);

            assertFalse(isApplicable);
        }

        @DisplayName("should return empty list when no one internship matched passed filter")
        @Test
        void shouldReturnEmptyListWhenNothingMatchedFilter() {
            filter.setNamePattern("Slavery work");

            var actualFilteredUsers = internshipNameFilter.apply(internshipsToFilter, filter);

            assertEquals(List.of(), actualFilteredUsers.toList());
        }
    }
}