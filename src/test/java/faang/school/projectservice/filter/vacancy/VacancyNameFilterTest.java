package faang.school.projectservice.filter.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.vacancy.Vacancy;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VacancyNameFilterTest {

    VacancyNameFilter vacancyNameFilter = new VacancyNameFilter();

    private VacancyFilterDto filterDto;
    private Vacancy vacancy1;
    private Vacancy vacancy2;

    @Test
    void isApplicable_whenNamePatternIsNotNull_shouldReturnTrue() {
        // given
        filterDto = VacancyFilterDto.builder().namePattern("Developer").build();
        // when/then
        assertTrue(vacancyNameFilter.isApplicable(filterDto));
    }

    @Test
    void isApplicable_whenNamePatternIsNull_shouldReturnFalse() {
        // given
        filterDto = VacancyFilterDto.builder().build();
        // when/then
        assertFalse(vacancyNameFilter.isApplicable(filterDto));
    }

    @Test
    void apply_whenNamePatternMatches_shouldFilterVacancies() {
        // given
        filterDto = VacancyFilterDto.builder().namePattern("Developer").build();
        vacancy1 = Vacancy.builder().name("Java Developer").build();
        vacancy2 = Vacancy.builder().name("Python Developer").build();
        Vacancy vacancy3 = Vacancy.builder().name("Project Manager").build();
        Stream<Vacancy> vacancies = Stream.of(vacancy1, vacancy2, vacancy3);
        // when
        Stream<Vacancy> filteredVacancies = vacancyNameFilter.apply(vacancies, filterDto);
        // then
        List<Vacancy> filteredList = filteredVacancies.toList();
        assertThat(filteredList).hasSize(2);
        assertThat(filteredList).contains(vacancy1, vacancy2);
        assertThat(filteredList).doesNotContain(vacancy3);
    }

    @Test
    void apply_whenNoVacancyMatchesNamePattern_shouldReturnEmptyStream() {
        // given
        filterDto = VacancyFilterDto.builder().namePattern("Analyst").build();
        vacancy1 = Vacancy.builder().name("Java Developer").build();
        vacancy2 = Vacancy.builder().name("Python Developer").build();
        Stream<Vacancy> vacancies = Stream.of(vacancy1, vacancy2);
        // when
        Stream<Vacancy> filteredVacancies = vacancyNameFilter.apply(vacancies, filterDto);
        // then
        List<Vacancy> filteredList = filteredVacancies.toList();
        assertThat(filteredList).isEmpty();
    }
}