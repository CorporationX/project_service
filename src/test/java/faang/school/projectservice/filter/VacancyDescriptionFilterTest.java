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
public class VacancyDescriptionFilterTest {
    private VacancyDescriptionFilter vacancyDescriptionFilter;
    private VacancyFilterDto vacancyFilterDto;

    @BeforeEach
    public void setUp() {
        vacancyDescriptionFilter = new VacancyDescriptionFilter();
        vacancyFilterDto = new VacancyFilterDto();
    }

    @Test
    public void testIsApplicableWithEmptyFilter() {
        boolean result = vacancyDescriptionFilter.isApplicable(vacancyFilterDto);

        assertFalse(result);
    }

    @Test
    public void testIsApplicableSuccessfully() {
        vacancyFilterDto.setDescriptionPattern("some description pattern");
        boolean result = vacancyDescriptionFilter.isApplicable(vacancyFilterDto);

        assertTrue(result);
    }

    @Test
    public void testApplySuccessfully() {
        vacancyFilterDto.setDescriptionPattern("some description");
        Vacancy vacancy = new Vacancy();
        vacancy.setDescription("some description");
        Vacancy anotherVacancy = new Vacancy();
        anotherVacancy.setDescription("another description");

        List<Vacancy> resultList = vacancyDescriptionFilter.apply(vacancyFilterDto, Stream.of(vacancy, anotherVacancy))
                .toList();

        assertEquals(1, resultList.size());
        assertTrue(resultList.contains(vacancy));
        assertFalse(resultList.contains(anotherVacancy));
    }
}
