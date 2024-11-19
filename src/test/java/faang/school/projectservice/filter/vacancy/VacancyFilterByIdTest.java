package faang.school.projectservice.filter.vacancy;


import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.service.vacancy.VacancyTestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VacancyFilterByIdTest {

    @Mock
    private VacancyFilterById vacancyFilterById;

    @Test
    void testIsApplicable() {
        Stream<Vacancy> vacancies = VacancyTestData.getVacanciesStream();
        VacancyFilterDto dto = VacancyTestData.getFilterDto();

        when(vacancyFilterById.isApplicable(dto)).thenReturn(true);
        when(vacancyFilterById.apply(vacancies, dto)).thenReturn(Stream.of(Vacancy.builder().id(1L).name("Alex").build()));

        boolean result = vacancyFilterById.isApplicable(dto);
        Stream<Vacancy> vacancyStream = vacancyFilterById.apply(vacancies, dto);
        assertTrue(result);
        assertTrue(vacancyStream.anyMatch(vacancy -> vacancy.getId() == 1));
    }
}
