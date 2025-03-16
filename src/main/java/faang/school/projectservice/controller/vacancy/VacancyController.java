package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.vacancy.OpenVacancyRequestDto;
import faang.school.projectservice.dto.vacancy.FilterVacancyRequestDto;
import faang.school.projectservice.dto.vacancy.VacancyResponseDto;
import faang.school.projectservice.dto.vacancy.UpdateVacancyRequestDto;
import faang.school.projectservice.service.VacancyServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class VacancyController {

    private final VacancyServiceImpl vacancyServiceImpl;

    public void openVacancy(OpenVacancyRequestDto requestDto) {
        vacancyServiceImpl.openVacancy(requestDto);
    }

    public VacancyResponseDto updateVacancy(UpdateVacancyRequestDto requestDto) {
        return vacancyServiceImpl.updateVacancy(requestDto);
    }

    public List<VacancyResponseDto> getFilteredVacancies(FilterVacancyRequestDto filterDto) {
        return vacancyServiceImpl.getFilteredVacancies(filterDto);
    }

    public VacancyResponseDto getVacancyById(long vacancyId) {
        return vacancyServiceImpl.getVacancyById(vacancyId);
    }
}
