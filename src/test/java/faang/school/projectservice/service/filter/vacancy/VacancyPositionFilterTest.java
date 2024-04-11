package faang.school.projectservice.service.filter.vacancy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class VacancyPositionFilterTest extends VacancyFiltersSetup {
    @InjectMocks
    VacancyPositionFilter vacancyPositionFilter;

    @Test
    void testFiltersAreApplicable(){
        assertTrue(vacancyPositionFilter.isApplicable(filter2));
        assertTrue(vacancyPositionFilter.isApplicable(filter3));
    }

    @Test
    void testFiltersAreNotApplicable() {
        assertFalse(vacancyPositionFilter.isApplicable(filter1));
    }

    @Test
    void testFilter1() {
        assertEquals(0, vacancyPositionFilter.apply(vacancyStream, filter1).size());
    }

    @Test
    void testFilter2() {
        assertEquals(2, vacancyPositionFilter.apply(vacancyStream, filter2).size());
    }
    @Test
    void testFilter3() {
        assertEquals(2, vacancyPositionFilter.apply(vacancyStream, filter3).size());
    }
}
