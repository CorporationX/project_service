package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.service.VacancyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static faang.school.projectservice.model.TeamRole.DESIGNER;
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
    public void positiveGetVacancyInfoById() {
        long vacancyId = 1;
        controller.getVacancyInfoBiId(vacancyId);
        verify(service, times(1)).getVacancyInfoById(vacancyId);
    }

    @Test
    public void positiveFindVacancy() {
        VacancyFilterDto filter = new VacancyFilterDto("name", DESIGNER);
        controller.findVacancy(filter);
        verify(service, times(1)).findVacancy(filter);
    }
}
