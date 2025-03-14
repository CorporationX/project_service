package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.VacancyCandidateDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.service.VacancyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static faang.school.projectservice.model.TeamRole.DESIGNER;
import static faang.school.projectservice.model.TeamRole.MANAGER;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VacancyControllerTest {
    private List<Candidate> candidates;

    @Mock
    private VacancyService service;

    @InjectMocks
    private VacancyController controller;

    @Test
    public void positiveCreateVacancy() {
        VacancyDto vacancyDto = VacancyDto.builder().name("Nescafe").salary(10).count(1).build();
        VacancyCandidateDto vacancyCandidateDto =
                VacancyCandidateDto.builder().name("Nescafe").count(1).build();
        when(service.createVacancy(vacancyDto)).thenReturn(vacancyCandidateDto);
        VacancyCandidateDto result = controller.createVacancy(vacancyDto);
        assertNotNull(result);
        verify(service, times(1)).createVacancy(vacancyDto);
    }

    @Test
    public void positiveUpdateVacancy() {
        long vacancyId = 1;
        VacancyDto vacancyDto = VacancyDto.builder().name("Nescafe").salary(10).count(1).build();
        VacancyCandidateDto vacancyCandidateDto =
                VacancyCandidateDto.builder().name("Nescafe").count(1).build();
        when(service.updateVacancy(vacancyId, vacancyDto)).thenReturn(vacancyCandidateDto);
        VacancyCandidateDto result = controller.updateVacancy(vacancyId, vacancyDto);
        assertNotNull(result);
        verify(service, times(1)).updateVacancy(vacancyId, vacancyDto);
    }

    @Test
    public void positiveGetVacancyInfoById() {
        long vacancyId = 1;
        VacancyCandidateDto vacancyCandidateDto =
                new VacancyCandidateDto(1, "bob", candidates, MANAGER);
        when(service.getVacancyInfoById(vacancyId)).thenReturn(vacancyCandidateDto);
        VacancyCandidateDto result = controller.getVacancyInfoBiId(vacancyId);
        assertNotNull(result);
        verify(service, times(1)).getVacancyInfoById(vacancyId);
    }

    @Test
    public void positiveFindVacancy() {
        VacancyFilterDto filter = new VacancyFilterDto("name", DESIGNER);
        List<VacancyCandidateDto> forFind = new ArrayList<>();
        when(service.findVacancy(filter)).thenReturn(forFind);
        List<VacancyCandidateDto> result = controller.findVacancy(filter);
        assertNotNull(result);
        verify(service, times(1)).findVacancy(filter);
    }
}
