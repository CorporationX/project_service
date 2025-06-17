package faang.school.projectservice.filter;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.filter.vacancy.PositionFilter;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class PositionFilterTest {
    private PositionFilter filter;
    private VacancyFilterDto filterDto;
    private List<Vacancy> testVacancies;

    @BeforeEach
    void setUp() {
        filter = new PositionFilter();
        filterDto = new VacancyFilterDto("Java", "DEVELOPER");

        testVacancies = List.of(
                Vacancy.builder()
                        .id(1L)
                        .name("Java Developer")
                        .position(TeamRole.DEVELOPER)
                        .build(),
                Vacancy.builder()
                        .id(2L)
                        .name("Python QA")
                        .position(TeamRole.MANAGER)
                        .build()
        );
    }

    @Test
    @DisplayName("isApplicable - true когда позиция не пустая")
    void isApplicable_shouldReturnTrue_whenPositionNotEmpty() {
        assertTrue(filter.isApplicable(filterDto));
    }

    @Test
    @DisplayName("apply - фильтрует по точному совпадению позиции")
    void testApply_shouldFilterByExactPositionMatch() {
        List<Vacancy> result = filter.apply(testVacancies.stream(), filterDto)
                .collect(Collectors.toList());

        assertEquals(1, result.size());
        assertEquals(TeamRole.DEVELOPER, result.get(0).getPosition());
    }
}

