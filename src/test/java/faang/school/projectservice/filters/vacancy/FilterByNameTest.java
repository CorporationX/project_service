package faang.school.projectservice.filters.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilterByNameTest {
    private static final int COUNT_VACANCIES = 3;
    private List<Vacancy> vacancyList;
    private VacancyFilterDto filterDto;

    private final FilterByName filter = new FilterByName();

    @BeforeEach
    void setUp() {
        vacancyList = getVacancyList();
        filterDto = VacancyFilterDto.builder()
                .namePattern("Vacancy").build();
    }

    @Test
    void testIsApplicable() {
        assertTrue(filter.isApplicable(filterDto));
    }

    @Test
    void testApply() {
        Stream<Vacancy> streamVacancies = vacancyList.stream();
        List<Vacancy> expectedVacancies = getExpectedVacanciesAfterFilter();

        List<Vacancy> result = filter.apply(streamVacancies, filterDto).toList();

        assertEquals(expectedVacancies, result);
    }

    private List<Vacancy> getVacancyList() {
        List<Vacancy> vacancies = new ArrayList<>(COUNT_VACANCIES);
        for (int i = 0; i < COUNT_VACANCIES; i++) {
            Vacancy vacancy = Vacancy.builder()
                    .id(i + 1L)
                    .name("Vacancy " + (i + 1))
                    .build();
            if (i == 1) {
                vacancy.setName("What is your name?");
            }
            vacancies.add(vacancy);
        }
        return vacancies;
    }

    private List<Vacancy> getExpectedVacanciesAfterFilter() {
        Vacancy vacancy1 = Vacancy.builder()
                .id(1L)
                .name("Vacancy 1")
                .build();
        Vacancy vacancy2 = Vacancy.builder()
                .id(3L)
                .name("Vacancy 3")
                .build();

        return List.of(vacancy1,
                vacancy2);
    }
}
