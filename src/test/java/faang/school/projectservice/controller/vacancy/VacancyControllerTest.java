package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.api.VacancyApi;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.service.UserService;
import faang.school.projectservice.service.VacancyService;
import faang.school.projectservice.validator.vacancy.VacancyValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Alexander Bulgakov
 */
@ExtendWith(MockitoExtension.class)
public class VacancyControllerTest {
    @Mock
    private VacancyService vacancyService;
    @Mock
    private VacancyValidator vacancyValidator;
    @Mock
    private UserService userService;
    @Mock
    private UserContext userContext;

    private VacancyApi vacancyController;

    @BeforeEach
    void setUp() {
        vacancyController = new VacancyController(vacancyService,
                vacancyValidator, userContext, userService);
    }

    @Test
    @DisplayName("Test get vacancy")
    void testGetVacancy() {
        long vacancyId = 1L;
        VacancyDto vacancyDto = new VacancyDto();
        when(vacancyService.getVacancyDto(vacancyId)).thenReturn(vacancyDto);

        VacancyDto actual = vacancyController.getVacancy(vacancyId);

        assertEquals(vacancyDto, actual);
        verify(vacancyService, times(1)).getVacancyDto(vacancyId);
    }

    @Test
    @DisplayName("Test get vacancies")
    void testGetVacancies() {
        VacancyFilterDto filter = new VacancyFilterDto();
        List<VacancyDto> vacancyList = new ArrayList<>();
        when(vacancyService.getVacancies(filter)).thenReturn(vacancyList);

        List<VacancyDto> actual = vacancyController.getVacancies(filter);

        assertEquals(vacancyList, actual);
        verify(vacancyService, times(1)).getVacancies(filter);
    }

    @Test
    void testCreate() {
        VacancyDto vacancyDto = new VacancyDto();
        when(vacancyService.createVacancy(vacancyDto)).thenReturn(vacancyDto);

        VacancyDto creation = vacancyController.create(vacancyDto);

        assertEquals(vacancyDto, creation);
        verify(vacancyValidator, times(1)).validateVacancy(vacancyDto);
        verify(vacancyService, times(1)).createVacancy(vacancyDto);
    }

    @Test
    void testUpdate() {
        VacancyDto vacancyDto = new VacancyDto();
        vacancyDto.setId(1L);

        UserDto user = new UserDto();
        user.setId(1L);

        when(userContext.getUserId()).thenReturn(1L);
        when(userService.getUserDtoById(1L)).thenReturn(user);
        when(vacancyService.updateVacancy(vacancyDto)).thenReturn(vacancyDto);

        VacancyDto updated = vacancyController.update(vacancyDto);

        assertEquals(vacancyDto, updated);
        verify(vacancyValidator, times(1)).validateUser(user, vacancyDto);
        verify(vacancyValidator, times(1)).validateSupervisorRole(user.getId());
        verify(vacancyValidator, times(1)).validateVacancy(vacancyDto);
        verify(vacancyService, times(1)).updateVacancy(vacancyDto);
    }

    @Test
    void testClose() {
        long vacancyId = 1L;
        UserDto user = new UserDto();
        user.setId(1L);

        VacancyDto vacancyDto = new VacancyDto();
        vacancyDto.setId(1L);
        vacancyDto.setStatus(VacancyStatus.CLOSED);

        when(userContext.getUserId()).thenReturn(1L);
        when(userService.getUserDtoById(1L)).thenReturn(user);
        when(vacancyService.closeVacancy(vacancyId)).thenReturn(vacancyDto);

        VacancyDto closed = vacancyController.close(vacancyId);

        assertEquals(vacancyDto, closed);
        verify(vacancyValidator, times(1)).validateSupervisorRole(user.getId());
        verify(vacancyService, times(1)).closeVacancy(vacancyId);
    }
}
