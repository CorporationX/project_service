package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.service.vacancy.VacancyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class VacancyControllerTest {
    private static final long VACANCY_ID = 1L;
    @Mock
    private VacancyService vacancyService;
    @InjectMocks
    private VacancyController vacancyController;
    private VacancyDto vacancyDto;

    @BeforeEach
    void setUp() {
        vacancyDto = new VacancyDto();
        vacancyDto.setId(VACANCY_ID);
    }


    @Test
    public void whenFindByIdThenGetVacancyDto() {
        when(vacancyService.findById(VACANCY_ID)).thenReturn(vacancyDto);
        assertThat(vacancyController.findById(VACANCY_ID)).isEqualTo(vacancyDto);
    }

    @Test
    public void whenCreateThenGetVacancyDto() {
        when(vacancyService.create(vacancyDto)).thenReturn(vacancyDto);
        assertThat(vacancyController.create(vacancyDto)).isEqualTo(vacancyDto);
    }

    @Test
    public void whenUpdateThenGetVacancyDto() {
        when(vacancyService.update(vacancyDto)).thenReturn(vacancyDto);
        assertThat(vacancyController.update(vacancyDto)).isEqualTo(vacancyDto);
    }

    @Test
    public void whenFindAllThenGetList() {
        when(vacancyService.findAllDto()).thenReturn(List.of(vacancyDto));
        assertThat(vacancyController.findAll()).isEqualTo(List.of(vacancyDto));
    }

    @Test
    public void whenFindAllWithFilterThenGetFilteredList() {
        when(vacancyService.findAllWithFilter(any())).thenReturn(List.of(vacancyDto));
        assertThat(vacancyController.findAllWithFilter(new VacancyFilterDto())).isEqualTo(List.of(vacancyDto));
    }

    @Test
    public void whenDeleteSuccessfully() {
        vacancyController.delete(VACANCY_ID);
        verify(vacancyService).delete(VACANCY_ID);
    }
}