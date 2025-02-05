package faang.school.projectservice.service.vacancy.filter;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class VacancyNameFilterTest {
    VacancyFilter filter = new VacancyNameFilter();

    @Test
    void isApplicable() {
        VacancyFilterDto vacancyFilterDto = VacancyFilterDto.builder()
                .name("test")
                .build();
        boolean actual = filter.isApplicable(vacancyFilterDto);
        Assertions.assertTrue(actual);
    }

    @Test
    void isApplicableIsDtoNull() {
        boolean actual = filter.isApplicable(null);
        Assertions.assertFalse(actual);
    }

    @Test
    void isApplicableIsNameNull() {
        VacancyFilterDto vacancyFilterDto = VacancyFilterDto.builder()
                .position(TeamRole.ANALYST)
                .build();
        boolean actual = filter.isApplicable(vacancyFilterDto);
        Assertions.assertFalse(actual);
    }

    @Test
    void isApplicableIsNameEmpty() {
        VacancyFilterDto vacancyFilterDto = VacancyFilterDto.builder()
                .name("")
                .build();
        boolean actual = filter.isApplicable(vacancyFilterDto);
        Assertions.assertFalse(actual);
    }

    @Test
    void apply() {
        VacancyFilterDto vacancyFilterDto = VacancyFilterDto.builder()
                .name("test")
                .build();

        List<Vacancy> vacancies = List.of(
                Vacancy.builder()
                        .id(1L)
                        .name("test")
                        .build(),
                Vacancy.builder()
                        .id(2L)
                        .name("vacancy")
                        .build(),
                Vacancy.builder()
                        .id(3L)
                        .name("vacancy-test_2")
                        .build(),
                Vacancy.builder()
                        .id(5L)
                        .build()
        );

        List<Vacancy> expectedVacancies = List.of(
                Vacancy.builder()
                        .id(1L)
                        .name("test")
                        .build(),
                Vacancy.builder()
                        .id(3L)
                        .name("vacancy-test_2")
                        .build()
        );
        List<Vacancy> actual = filter.apply(vacancies.stream(), vacancyFilterDto).toList();
        Assertions.assertEquals(expectedVacancies, actual);
    }

    @Test
    void applyVacanciesIsNull() {
        VacancyFilterDto vacancyFilterDto = VacancyFilterDto.builder()
                .name("test")
                .build();

        List<Vacancy> actual = filter.apply(null, vacancyFilterDto).toList();
        Assertions.assertTrue(actual.isEmpty());
    }
}