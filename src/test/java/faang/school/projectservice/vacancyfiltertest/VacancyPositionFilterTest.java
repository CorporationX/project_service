package faang.school.projectservice.vacancyfiltertest;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.filter.vacancy.VacancyFilter;
import faang.school.projectservice.filter.vacancy.VacancyPositionFilter;
import faang.school.projectservice.model.Vacancy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static faang.school.projectservice.model.TeamRole.DESIGNER;
import static faang.school.projectservice.model.TeamRole.DEVELOPER;
import static faang.school.projectservice.model.TeamRole.OWNER;
import static faang.school.projectservice.model.TeamRole.TESTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class VacancyPositionFilterTest {
    private VacancyFilterDto filterDto;
    private VacancyFilter filter;
    private final Vacancy firstVacancy = Vacancy.builder().build();
    private final Vacancy secondVacancy = Vacancy.builder().build();

    @BeforeEach
    public void setUp() {
        filter = new VacancyPositionFilter();
        filterDto = VacancyFilterDto.builder().positionPattern(TESTER).build();
    }

    @Test
    public void positiveApplicableWithSetPosition() {
        assertTrue(filter.isApplicable(filterDto));
    }

    @Test
    public void positiveOneMatchApplyFilter() {
        firstVacancy.setPosition(OWNER);
        secondVacancy.setPosition(TESTER);
        List<Vacancy> result = prepareFilter(firstVacancy, secondVacancy);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(TESTER, result.get(0).getPosition());
    }

    @Test
    public void positiveMultipleMatchApplyFilter() {
        firstVacancy.setPosition(TESTER);
        secondVacancy.setPosition(TESTER);
        List<Vacancy> result = prepareFilter(firstVacancy, secondVacancy);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(TESTER, result.get(0).getPosition());
        assertEquals(TESTER, result.get(1).getPosition());
    }

    @Test
    public void negativeNotAnyMatchFilter() {
        firstVacancy.setPosition(DEVELOPER);
        secondVacancy.setPosition(DESIGNER);
        List<Vacancy> result = prepareFilter(firstVacancy, secondVacancy);
        assertTrue(result.isEmpty());
    }

    @Test
    public void negativeNotApplicableWithNullPosition() {
        firstVacancy.setPosition(null);
        secondVacancy.setPosition(null);
        List<Vacancy> result = prepareFilter(firstVacancy, secondVacancy);
        assertTrue(result.isEmpty());
    }

    private List<Vacancy> prepareFilter(Vacancy firstVacancy, Vacancy secondVacancy) {
        Stream<Vacancy> vacancyStream = Stream.of(firstVacancy, secondVacancy);
        Stream<Vacancy> filteredVacancies = filter.apply(vacancyStream, filterDto);
        return filteredVacancies.toList();
    }

}
