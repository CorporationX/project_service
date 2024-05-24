package faang.school.projectservice.filter;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.service.vacancy.filters.VacancyFilterByName;
import faang.school.projectservice.model.Vacancy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class VacancyFilterTest {
    @Spy
    private VacancyFilterByName vacancyFilter;
    private VacancyFilterDto vacancyFilterDto;
    private Vacancy vacancy1;
    private Vacancy vacancy2;

    @BeforeEach
    public void init() {
        vacancyFilterDto = new VacancyFilterDto();

        vacancy1 = new Vacancy();
        vacancy1.setName("Java");
        vacancy1.setCount(9999);

        vacancy2 = new Vacancy();
        vacancy2.setName("Python");
        vacancy2.setCount(99);
    }

    @Test
    public void testVacancyFilterIsAccept() {
        vacancyFilterDto.setName("Java");
        vacancyFilterDto.setCount(9999L);
        assertTrue(vacancyFilter.isAcceptable(vacancyFilterDto));
    }

    @Test
    public void testVacancyFilterIsNotAccept() {
        vacancyFilterDto.setName(null);
        vacancyFilterDto.setCount(0L);
        assertFalse(vacancyFilter.isAcceptable(vacancyFilterDto));
    }

    @Test
    public void testApplyFilter() {
        vacancyFilterDto.setName("Java");
        vacancyFilterDto.setCount(3000L);
        Vacancy[] expected = new Vacancy[]{vacancy1};
        Stream<Vacancy> out = vacancyFilter.applyFilter(Stream.of(vacancy1, vacancy2), vacancyFilterDto);
        assertArrayEquals(expected, out.toArray());
    }
}
