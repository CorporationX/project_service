package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.FilterVacancyRequestDto;
import faang.school.projectservice.dto.vacancy.OpenVacancyRequestDto;
import faang.school.projectservice.dto.vacancy.UpdateVacancyRequestDto;
import faang.school.projectservice.dto.vacancy.VacancyResponseDto;

import java.util.List;

public interface VacancyService {
    void openVacancy(OpenVacancyRequestDto requestDto);
    VacancyResponseDto updateVacancy(UpdateVacancyRequestDto requestDto);
    List<VacancyResponseDto> getFilteredVacancies(FilterVacancyRequestDto filterDto);
    VacancyResponseDto getVacancyById(long vacancyId);
}
