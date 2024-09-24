package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.filter.VacancyFilterDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public interface VacancyService {

    @Transactional
    VacancyDto createVacancy(VacancyDto vacancyDto);

    @Transactional
    VacancyDto updateVacancy(Long vacancyId, VacancyDto vacancyDto);

    void deleteVacancy(Long vacancyId);

    List<VacancyDto> getVacancies(VacancyFilterDto vacancyFilterDto);

    VacancyDto getVacancyById(Long vacancyId);
}
