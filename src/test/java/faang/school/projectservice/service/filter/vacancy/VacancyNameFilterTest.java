package faang.school.projectservice.service.filter.vacancy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class VacancyNameFilterTest extends VacancyFiltersSetup {
    @InjectMocks
    VacancyNameFilter vacancyNameFilter;

    @Test
    void testFiltersAreApplicable(){
        assertTrue(vacancyNameFilter.isApplicable(filter1));
        assertTrue(vacancyNameFilter.isApplicable(filter3));
    }

    @Test
    void testFiltersAreNotApplicable() {
        assertFalse(vacancyNameFilter.isApplicable(filter2));
    }

    @Test
    void testFilter1() {
        assertEquals(2, vacancyNameFilter.apply(vacancyStream, filter1).size());
    }

    @Test
    void testFilter2() {
        assertEquals(0, vacancyNameFilter.apply(vacancyStream, filter2).size());
    }
    @Test
    void testFilter3() {
        assertEquals(2, vacancyNameFilter.apply(vacancyStream, filter3).size());
    }
}
