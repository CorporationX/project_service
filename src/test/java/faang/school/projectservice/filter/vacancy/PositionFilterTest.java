package faang.school.projectservice.filter.vacancy;

import faang.school.projectservice.dto.vacancy.FilterVacancyRequestDto;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class PositionFilterTest {

    @InjectMocks
    private PositionFilter positionFilter;

    @Test
    public void testIsApplicable_PositionNull_ReturnsFalse() {
        var filterDto = new FilterVacancyRequestDto(null, null);

        var result = positionFilter.isApplicable(filterDto);

        assertFalse(result);
    }

    @Test
    public void testIsApplicable_PositionPresents_ReturnsTrue() {
        var filterDto = new FilterVacancyRequestDto(TeamRole.ANALYST, null);

        var result = positionFilter.isApplicable(filterDto);

        assertTrue(result);
    }

    @Test
    public void testApply_HaveMatchedVacancies_ReturnsNonEmptyStream() {
        var filterDto = new FilterVacancyRequestDto(TeamRole.ANALYST, null);
        var expectedItems = generateExpectedItems();
        var source = generateSource(expectedItems);

        var result = positionFilter.apply(source, filterDto);

        assertIterableEquals(expectedItems, result.toList());
    }

    private static List<Vacancy> generateExpectedItems() {
        return List.of(
                Vacancy.builder().id(1L).position(TeamRole.ANALYST).build(),
                Vacancy.builder().id(2L).position(TeamRole.ANALYST).build());
    }

    private static Stream<Vacancy> generateSource(List<Vacancy> expectedItems) {
        return Stream.concat(
                expectedItems.stream(),
                Stream.of(
                        Vacancy.builder().id(10L).position(TeamRole.INTERN).build(),
                        Vacancy.builder().id(11L).position(TeamRole.DEVELOPER).build()));
    }
}