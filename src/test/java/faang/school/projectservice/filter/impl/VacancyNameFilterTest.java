package faang.school.projectservice.filter.impl;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.filter.vacancy.impl.VacancyNameFilter;
import faang.school.projectservice.model.Vacancy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Alexander Bulgakov
 */
@ExtendWith(MockitoExtension.class)
public class VacancyNameFilterTest {

    private VacancyNameFilter vacancyNameFilter;
    private List<Vacancy> vacancies;

    @BeforeEach
    public void setUp() {
        vacancyNameFilter = new VacancyNameFilter();
        Vacancy vacancy1 = new Vacancy();
        vacancy1.setName("Software Engineer");

        Vacancy vacancy2 = new Vacancy();
        vacancy2.setName("Data Analyst");

        Vacancy vacancy3 = new Vacancy();
        vacancy3.setName("Product Manager");
        vacancies = new ArrayList<>();

        // Add some sample vacancies for testing
        vacancies.add(vacancy1);
        vacancies.add(vacancy2);
        vacancies.add(vacancy3);
    }

    @Test
    public void testIsApplicable() {
        VacancyFilterDto filter = new VacancyFilterDto();
        filter.setName("Engineer");

        boolean isApplicable = vacancyNameFilter.isApplicable(filter);

        assertEquals(true, isApplicable);
    }

    @Test
    public void testApply() {
        VacancyFilterDto filter = new VacancyFilterDto();
        filter.setName("Engineer");

        vacancyNameFilter.apply(vacancies, filter);

        assertEquals(1, vacancies.size());
        assertEquals("Software Engineer", vacancies.get(0).getName());
    }
}
