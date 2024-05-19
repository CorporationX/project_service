package faang.school.projectservice.service.vacancy.filter;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class VacancyNameFilterTest {
    private final VacancyFilter filter = new VacancyNameFilter();
    private final VacancyFilterDto vacancyFilterDto = new VacancyFilterDto();
    private final VacancyDto firstVacancy = new VacancyDto();
    private final VacancyDto secondVacancy = new VacancyDto();
    private Stream<VacancyDto> streamVacancy;

    @BeforeEach
    void setUp() {
        firstVacancy.setName("name");
        secondVacancy.setName("nm");
        streamVacancy = Stream.of(firstVacancy, secondVacancy);
    }

    @Test
    public void whenVacancyFilterDtoIsNotApplicableThenFalse() {
        assertThat(filter.isApplicable(vacancyFilterDto)).isFalse();
    }

    @Test
    public void whenVacancyFilterDtoIsApplicableThenTrue() {
        vacancyFilterDto.setName("nm");
        assertThat(filter.isApplicable(vacancyFilterDto)).isTrue();
    }

    @Test
    public void whenFilterThenGetFilteredStream() {
        vacancyFilterDto.setName("nm");
        List<VacancyDto> result = filter.filter(streamVacancy, vacancyFilterDto).toList();
        assertThat(result).isEqualTo(List.of(secondVacancy));
    }
}