package faang.school.projectservice.filter.impl;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.filter.vacancy.VacancyPositionFilter;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Alexander Bulgakov
 */

@ExtendWith(MockitoExtension.class)
public class VacancyPositionFilterTest {
    private VacancyPositionFilter vacancyPositionFilter;
    private List<Vacancy> vacancies;

    @BeforeEach
    public void setUp() {
        vacancyPositionFilter = new VacancyPositionFilter();
        Vacancy vacancy1 = new Vacancy();
        vacancy1.setName("Software Engineer");
        vacancy1.setPosition(TeamRole.DEVELOPER);

        Vacancy vacancy2 = new Vacancy();
        vacancy2.setName("Data Analyst");
        vacancy2.setPosition(TeamRole.ANALYST);

        Vacancy vacancy3 = new Vacancy();
        vacancy3.setName("Product Manager");
        vacancy3.setPosition(TeamRole.MANAGER);

        vacancies = new ArrayList<>();

        vacancies.add(vacancy1);
        vacancies.add(vacancy2);
        vacancies.add(vacancy3);
    }

    @Test
    public void testIsApplicable() {
        VacancyFilterDto filter = new VacancyFilterDto();
        filter.setPosition(TeamRole.DEVELOPER);

        boolean isApplicable = vacancyPositionFilter.isApplicable(filter);

        assertTrue(isApplicable);
    }

    @Test
    public void testApply() {
        VacancyFilterDto filter = new VacancyFilterDto();
        filter.setPosition(TeamRole.DEVELOPER);

        vacancyPositionFilter.apply(vacancies, filter);
        var expected = vacancies.get(0);

        assertEquals(1, vacancies.size());
        assertEquals("Software Engineer", expected.getName());
    }
}
