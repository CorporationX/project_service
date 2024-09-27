package faang.school.projectservice.filter;

import faang.school.projectservice.model.Vacancy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class VacancyNameFilterTest {
    private VacancyNameFilter vacancyNameFilter;
    private VacancyFilterDto vacancyFilterDto;

    @BeforeEach
    public void setUp() {
        vacancyNameFilter = new VacancyNameFilter();
        vacancyFilterDto = new VacancyFilterDto();
    }

    @Test
    public void testIsApplicableWithEmptyFilter() {
        boolean result = vacancyNameFilter.isApplicable(vacancyFilterDto);

        assertFalse(result);
    }

    @Test
    public void testIsApplicableSuccessfully() {
        vacancyFilterDto.setNamePattern("name pattern");
        boolean result = vacancyNameFilter.isApplicable(vacancyFilterDto);

        assertTrue(result);
    }

    @Test
    public void testApplySuccessfully() {
        vacancyFilterDto.setNamePattern("some name");
        Vacancy vacancy = new Vacancy();
        vacancy.setName("some name");
        Vacancy anotherVacancy = new Vacancy();
        anotherVacancy.setName("name");

        List<Vacancy> resultList = vacancyNameFilter.apply(vacancyFilterDto, Stream.of(vacancy, anotherVacancy))
                .toList();

        assertEquals(1, resultList.size());
        assertTrue(resultList.contains(vacancy));
        assertFalse(resultList.contains(anotherVacancy));
    }
}
