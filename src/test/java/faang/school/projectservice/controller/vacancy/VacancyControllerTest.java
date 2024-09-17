package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.service.vacancy.VacancyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class VacancyControllerTest {

    @InjectMocks
    private VacancyController vacancyController;

    @Mock
    private VacancyService vacancyService;

    private final long id = 1L;
    private VacancyDto dto;

    @Test
    void create_isOk() {
        // given
        dto = VacancyDto.builder().build();
        // when
        vacancyController.create(dto);
        // then
        verify(vacancyService).create(dto);
    }

    @Test
    void update_isOk() {
        // given
        dto = VacancyDto.builder().build();
        // when
        vacancyController.update(dto);
        // then
        verify(vacancyService).update(dto);
    }

    @Test
    void delete_isOk() {
        // given/when
        vacancyController.delete(id);
        // then
        verify(vacancyService).delete(id);
    }

    @Test
    void findAll_isOk() {
        // given
        VacancyFilterDto filter = VacancyFilterDto.builder().build();
        // when
        vacancyController.findAll(filter);
        // then
        verify(vacancyService).findAll(filter);
    }

    @Test
    void findById_isOk() {
        // given/when
        vacancyController.findById(id);
        // then
        verify(vacancyService).findById(id);
    }
}