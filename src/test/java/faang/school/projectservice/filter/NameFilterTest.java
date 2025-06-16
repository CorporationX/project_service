package faang.school.projectservice.filter;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.filter.vacancy.NameFilter;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class NameFilterTest {
    private NameFilter filter;
    private VacancyFilterDto filterDto;
    private List<Vacancy> testVacancies;

    @BeforeEach
    void setUp() {
        filter = new NameFilter();
        filterDto = new VacancyFilterDto("Java", "DEVELOPER");

        testVacancies = List.of(
                Vacancy.builder()
                        .id(1L)
                        .name("Java Developer")
                        .position(TeamRole.DEVELOPER)
                        .build(),
                Vacancy.builder()
                        .id(2L)
                        .name("Python Developer")
                        .position(TeamRole.DEVELOPER)
                        .build()
        );
    }

    @Test
    @DisplayName("isApplicable - true когда имя не пустое")
    void isApplicable_shouldReturnTrue_whenNameNotEmpty() {
        assertTrue(filter.isApplicable(filterDto));
    }

    @Test
    @DisplayName("isApplicable - false когда имя пустое")
    void isApplicable_shouldReturnFalse_whenNameBlank() {
        filterDto.setName(" ");
        assertFalse(filter.isApplicable(filterDto));
    }

    @Test
    @DisplayName("apply - фильтрует по частичному совпадению имени")
    void testApply_shouldFilterByPartialNameMatch() {
        List<Vacancy> result = filter.apply(testVacancies.stream(), filterDto)
                .collect(Collectors.toList());

        assertEquals(1, result.size());
        assertEquals("Java Developer", result.get(0).getName());
    }
}
