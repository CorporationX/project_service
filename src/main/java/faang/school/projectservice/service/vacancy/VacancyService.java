package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.vacancy.CloseVacancyDto;
import faang.school.projectservice.dto.vacancy.CreateVacancyDto;
import faang.school.projectservice.dto.vacancy.UpdateVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.VacancyResponseDto;

import java.util.List;

public interface VacancyService {
    VacancyResponseDto createVacancy(CreateVacancyDto dto);

    VacancyResponseDto updateVacancy(UpdateVacancyDto dto);

    VacancyResponseDto closeVacancy(Long id, CloseVacancyDto dto);

    List<VacancyResponseDto> getFilteredVacancies(VacancyFilterDto filterDto);

    VacancyResponseDto getVacancyById(Long id);
}