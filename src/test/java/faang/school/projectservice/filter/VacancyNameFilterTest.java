package faang.school.projectservice.filter;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.service.filter.VacancyNameFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

public class VacancyNameFilterTest {
    private VacancyNameFilter vacancyNameFilter;
    private List<Vacancy> vacancies;

    @BeforeEach
    public void setup() {
        vacancyNameFilter = new VacancyNameFilter();
        Vacancy vacancy1 = new Vacancy();
        vacancy1.setName("Java developer");

        Vacancy vacancy2 = new Vacancy();
        vacancy2.setName("Python developer");

        vacancies = List.of(vacancy1, vacancy2);
    }

    @Test
    public void testIsApplicable() {
        VacancyFilterDto vacancyFilterDto = new VacancyFilterDto();
        vacancyFilterDto.setName("developer");

        boolean result = vacancyNameFilter.isApplicable(vacancyFilterDto);

        Assertions.assertTrue(result);
    }

    @Test
    public void testIsNotApplicable(){
        VacancyFilterDto vacancyFilterDto = new VacancyFilterDto();

        boolean result = vacancyNameFilter.isApplicable(vacancyFilterDto);

        Assertions.assertFalse(result);
    }
    @Test
    public void testApply() {
        VacancyFilterDto vacancyFilterDto = new VacancyFilterDto();
        vacancyFilterDto.setName("Java developer");

        Stream<Vacancy> resultStream = vacancyNameFilter.apply(vacancies.stream(), vacancyFilterDto);
        List<Vacancy> resultFilter = resultStream.toList();
        Assertions.assertEquals(1, resultFilter.size());
        Assertions.assertEquals("Java developer", resultFilter.get(0).getName());
    }
}
