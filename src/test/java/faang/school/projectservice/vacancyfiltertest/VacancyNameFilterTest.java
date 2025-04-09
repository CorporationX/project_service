package faang.school.projectservice.vacancyfiltertest;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.filter.vacancy.VacancyFilter;
import faang.school.projectservice.filter.vacancy.VacancyNameFilter;
import faang.school.projectservice.model.Vacancy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class VacancyNameFilterTest {
    private VacancyFilterDto filterDto;
    private VacancyFilter filter;
    private final Vacancy firstVacancy = Vacancy.builder().build();
    private final Vacancy secondVacancy = Vacancy.builder().build();

    @BeforeEach
    public void setUp() {
        filter = new VacancyNameFilter();
        filterDto = VacancyFilterDto
                .builder()
                .namePattern("Java")
                .build();
    }

    @Test
    public void positiveIsApplicableWithSetName() {
        assertTrue(filter.isApplicable(filterDto));
    }

    @Test
    public void positiveOneMatchApplyFilter() {
        firstVacancy.setName("java");
        secondVacancy.setName("python");
        List<Vacancy> result = prepareFilter(firstVacancy, secondVacancy);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("java", result.get(0).getName());
    }

    @Test
    public void positiveMultipleMatchApplyFilter() {
        firstVacancy.setName("java");
        secondVacancy.setName("Java");
        List<Vacancy> result = prepareFilter(firstVacancy, secondVacancy);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("java", result.get(0).getName());
        assertEquals("Java", result.get(1).getName());
    }

    @Test
    public void negativeNotAnyMatchFilter() {
        firstVacancy.setName("CSharp");
        secondVacancy.setName("Python");
        List<Vacancy> result = prepareFilter(firstVacancy, secondVacancy);
        assertTrue(result.isEmpty());
    }

    @Test
    public void negativeNoApplicableBlankName() {
        firstVacancy.setName(" ");
        secondVacancy.setName("  ");
        List<Vacancy> result = prepareFilter(firstVacancy, secondVacancy);
        assertTrue(result.isEmpty());
    }

    @Test
    public void negativeNoApplicableEmptyName() {
        firstVacancy.setName("");
        secondVacancy.setName("");
        List<Vacancy> result = prepareFilter(firstVacancy, secondVacancy);
        assertTrue(result.isEmpty());
    }

    @Test
    public void negativeNoApplicableNullName() {
        firstVacancy.setName(null);
        secondVacancy.setName(null);
        List<Vacancy> result = prepareFilter(firstVacancy, secondVacancy);
        assertTrue(result.isEmpty());
    }

    private List<Vacancy> prepareFilter(Vacancy firstVacancy, Vacancy secondVacancy) {
        Stream<Vacancy> vacancyStream = Stream.of(firstVacancy, secondVacancy);
        Stream<Vacancy> filteredVacancies = filter.apply(vacancyStream, filterDto);
        return filteredVacancies.toList();
    }

}
