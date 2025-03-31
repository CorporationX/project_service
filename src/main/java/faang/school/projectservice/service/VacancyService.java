package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.FilterVacancyRequestDto;
import faang.school.projectservice.dto.vacancy.OpenVacancyRequestDto;
import faang.school.projectservice.dto.vacancy.UpdateVacancyRequestDto;
import faang.school.projectservice.dto.vacancy.VacancyResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public interface VacancyService {
    void openVacancy(OpenVacancyRequestDto requestDto);

    VacancyResponseDto updateVacancy(UpdateVacancyRequestDto requestDto);

    List<VacancyResponseDto> getFilteredVacancies(FilterVacancyRequestDto filterDto);

    Optional<VacancyResponseDto> getVacancyById(long vacancyId);

    String addOrChangeCoverToVacancy(long vacancyId, MultipartFile cover);

    InputStream getVacancyCover(long vacancyId);

    void deleteCoverFromVacancy(long vacancyId);
}
