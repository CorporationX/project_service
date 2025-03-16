package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.vacancy.OpenVacancyRequestDto;
import faang.school.projectservice.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class VacancyController {

    private final VacancyService vacancyService;

    public void openVacancy(OpenVacancyRequestDto requestDto) {
        vacancyService.openVacancy(requestDto);
    }
}
