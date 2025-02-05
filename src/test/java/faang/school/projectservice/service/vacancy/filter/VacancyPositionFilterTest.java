package faang.school.projectservice.service.vacancy.filter;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class VacancyPositionFilterTest {
    VacancyFilter filter = new VacancyPositionFilter();

    @Test
    void isApplicable() {
        VacancyFilterDto vacancyFilterDto = VacancyFilterDto.builder()
                .position(TeamRole.ANALYST)
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
    void isApplicableIsPositionNull() {
        VacancyFilterDto vacancyFilterDto = VacancyFilterDto.builder()
                .name("name")
                .build();
        boolean actual = filter.isApplicable(vacancyFilterDto);
        Assertions.assertFalse(actual);
    }

    @Test
    void apply() {
        VacancyFilterDto vacancyFilterDto = VacancyFilterDto.builder()
                .position(TeamRole.ANALYST)
                .build();

        List<Vacancy> vacancies = List.of(
                Vacancy.builder()
                        .id(1L)
                        .position(TeamRole.ANALYST)
                        .build(),
                Vacancy.builder()
                        .id(2L)
                        .position(TeamRole.TESTER)
                        .build(),
                Vacancy.builder()
                        .id(3L)
                        .position(TeamRole.ANALYST)
                        .build(),
                Vacancy.builder()
                        .id(5L)
                        .build()
        );

        List<Vacancy> expectedVacancies = List.of(
                Vacancy.builder()
                        .id(1L)
                        .position(TeamRole.ANALYST)
                        .build(),
                Vacancy.builder()
                        .id(3L)
                        .position(TeamRole.ANALYST)
                        .build()
        );
        List<Vacancy> actual = filter.apply(vacancies.stream(), vacancyFilterDto).toList();
        Assertions.assertEquals(expectedVacancies, actual);
    }

    @Test
    void applyVacanciesIsNull() {
        VacancyFilterDto vacancyFilterDto = VacancyFilterDto.builder()
                .position(TeamRole.ANALYST)
                .build();

        List<Vacancy> actual = filter.apply(null, vacancyFilterDto).toList();
        Assertions.assertTrue(actual.isEmpty());
    }
}