package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.vacancy.OpenVacancyRequestDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterRequestDto;
import faang.school.projectservice.dto.vacancy.VacancyResponseDto;
import faang.school.projectservice.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class VacancyController {

    private final VacancyService vacancyService;

    public void openVacancy(OpenVacancyRequestDto requestDto) {
        vacancyService.openVacancy(requestDto);
    }

    public List<VacancyResponseDto> getFilteredVacancies(VacancyFilterRequestDto filterDto) {
        return vacancyService.getFilteredVacancies(filterDto);
    }

    public VacancyResponseDto getVacancyById(long vacancyId) {
        return vacancyService.getVacancyById(vacancyId);
    }
}
