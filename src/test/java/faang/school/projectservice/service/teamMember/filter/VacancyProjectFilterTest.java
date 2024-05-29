package faang.school.projectservice.service.teamMember.filter;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.service.vacancy.filter.VacancyFilter;
import faang.school.projectservice.service.vacancy.filter.VacancyProjectFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class VacancyProjectFilterTest {
    private static final long FIRST_PROJECT_ID = 1L;
    private static final long SECOND_PROJECT_ID = 2L;
    private final VacancyFilter filter = new VacancyProjectFilter();
    private final VacancyFilterDto vacancyFilterDto = new VacancyFilterDto();
    private final VacancyDto firstVacancy = new VacancyDto();
    private final VacancyDto secondVacancy = new VacancyDto();
    private Stream<VacancyDto> streamVacancy;

    @BeforeEach
    void setUp() {
        firstVacancy.setProjectId(FIRST_PROJECT_ID);
        secondVacancy.setProjectId(SECOND_PROJECT_ID);
        streamVacancy = Stream.of(firstVacancy, secondVacancy);
    }

    @Test
    public void whenVacancyFilterDtoIsNotApplicableThenFalse() {
        assertThat(filter.isApplicable(vacancyFilterDto)).isFalse();
    }

    @Test
    public void whenVacancyFilterDtoIsApplicableThenTrue() {
        vacancyFilterDto.setProjectId(SECOND_PROJECT_ID);
        assertThat(filter.isApplicable(vacancyFilterDto)).isTrue();
    }

    @Test
    public void whenFilterThenGetFilteredStream() {
        vacancyFilterDto.setProjectId(SECOND_PROJECT_ID);
        List<VacancyDto> result = filter.filter(streamVacancy, vacancyFilterDto).toList();
        assertThat(result).isEqualTo(List.of(secondVacancy));
    }
}