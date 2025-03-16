package faang.school.projectservice.filter.vacancy;

import faang.school.projectservice.dto.vacancy.FilterVacancyRequestDto;
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
public class NameFilterTest {

    @InjectMocks
    private NameFilter nameFilter;

    @Test
    public void testIsApplicable_NameNull_ReturnsFalse() {
        var filterDto = new FilterVacancyRequestDto(null, null);

        var result = nameFilter.isApplicable(filterDto);

        assertFalse(result);
    }

    @Test
    public void testIsApplicable_NameEmpty_ReturnsFalse() {
        var filterDto = new FilterVacancyRequestDto(null, "");

        var result = nameFilter.isApplicable(filterDto);

        assertFalse(result);
    }

    @Test
    public void testIsApplicable_NameBlank_ReturnsFalse() {
        var filterDto = new FilterVacancyRequestDto(null, "   ");

        var result = nameFilter.isApplicable(filterDto);

        assertFalse(result);
    }

    @Test
    public void testIsApplicable_NameNotBlank_ReturnsTrue() {
        var filterDto = new FilterVacancyRequestDto(null, "Test");

        var result = nameFilter.isApplicable(filterDto);

        assertTrue(result);
    }

    @Test
    public void testApply_HaveMatchedVacancies_ReturnsNonEmptyStream() {
        var filterDto = new FilterVacancyRequestDto(null, "[a-z ]+");
        var expectedItems = generateExpectedItems();
        var source = generateSource(expectedItems);

        var result = nameFilter.apply(source, filterDto);

        assertIterableEquals(expectedItems, result.toList());
    }

    private static List<Vacancy> generateExpectedItems() {
        return List.of(
                Vacancy.builder()
                        .id(1L)
                        .name("first vacancy")
                        .build(),
                Vacancy.builder()
                        .id(2L)
                        .name("vacancy")
                        .build());
    }

    private static Stream<Vacancy> generateSource(List<Vacancy> expectedItems) {
        return Stream.concat(
                expectedItems.stream(),
                Stream.of(
                        Vacancy.builder()
                                .id(10L)
                                .name("2nd vacancy")
                                .build(),
                        Vacancy.builder()
                                .id(11L)
                                .name("Third vacancy")
                                .build()));
    }
}