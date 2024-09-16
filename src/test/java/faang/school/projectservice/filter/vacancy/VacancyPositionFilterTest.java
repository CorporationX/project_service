package faang.school.projectservice.filter.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.vacancy.Vacancy;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class VacancyPositionFilterTest {

    VacancyPositionFilter vacancyPositionFilter = new VacancyPositionFilter();

    private VacancyFilterDto filterDto;
    private Vacancy vacancy1;
    private Vacancy vacancy2;

    @Test
    void isApplicable_whenPositionPatternIsNotNull_shouldReturnTrue() {
        // given
        filterDto = VacancyFilterDto.builder().positionPattern("Developer").build();
        // when/then
        assertTrue(vacancyPositionFilter.isApplicable(filterDto));
    }

    @Test
    void isApplicable_whenPositionPatternIsNull_shouldReturnFalse() {
        // given
        filterDto = VacancyFilterDto.builder().build();
        // when/then
        assertFalse(vacancyPositionFilter.isApplicable(filterDto));
    }

    @Test
    void apply_whenPositionPatternMatches_shouldFilterVacancies() {
        // given
        filterDto = VacancyFilterDto.builder().positionPattern("Developer").build();
        vacancy1 = Vacancy.builder().description("Java Developer").build();
        vacancy2 = Vacancy.builder().description("Python Developer").build();
        Vacancy vacancy3 = Vacancy.builder().description("Project Manager").build();
        Stream<Vacancy> vacancies = Stream.of(vacancy1, vacancy2, vacancy3);
        // when
        Stream<Vacancy> filteredVacancies = vacancyPositionFilter.apply(vacancies, filterDto);
        // then
        List<Vacancy> filteredList = filteredVacancies.toList();
        assertThat(filteredList).hasSize(2);
        assertThat(filteredList).contains(vacancy1, vacancy2);
        assertThat(filteredList).doesNotContain(vacancy3);
    }

    @Test
    void apply_whenNoVacancyMatchesNamePattern_shouldReturnEmptyStream() {
        // given
        filterDto = VacancyFilterDto.builder().positionPattern("Analyst").build();
        vacancy1 = Vacancy.builder().description("Java Developer").build();
        vacancy2 = Vacancy.builder().description("Python Developer").build();
        Stream<Vacancy> vacancies = Stream.of(vacancy1, vacancy2);
        // when
        Stream<Vacancy> filteredVacancies = vacancyPositionFilter.apply(vacancies, filterDto);
        // then
        List<Vacancy> filteredList = filteredVacancies.toList();
        assertThat(filteredList).isEmpty();
    }
}