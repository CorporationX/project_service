package faang.school.projectservice.filter.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.service.vacancy.VacancyTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VacancyFilterByNameTest {

    @Mock
    private VacancyFilterByName vacancyFilterByName;

    @Test
    @DisplayName("Test filter by name")
    void testFilterByName() {
        VacancyFilterDto dto = VacancyTestData.getFilterDto();
        Stream<Vacancy> vacancyStream = VacancyTestData.getVacanciesStream();

        when(vacancyFilterByName.isApplicable(dto)).thenReturn(true);
        when(vacancyFilterByName.apply(vacancyStream, dto)).thenReturn(Stream.of(Vacancy.builder().id(1L).name("Alex").build(),
                Vacancy.builder().id(3L).name("Alex").build()));

        Stream<Vacancy> inspectedStream = vacancyFilterByName.apply(vacancyStream, dto);
        boolean validationDto = vacancyFilterByName.isApplicable(dto);
        assertTrue(validationDto);
        assertTrue(inspectedStream.allMatch(vacancy -> vacancy.getName().equals("Alex")));
    }
}
