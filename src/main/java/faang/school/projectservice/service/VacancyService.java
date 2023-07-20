package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.mappper.VacancyMapper;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;

    public VacancyDto createVacancy(VacancyDto vacancyDto) {

        Vacancy savedVacancy = vacancyRepository.save(vacancyMapper.toEntity(vacancyDto));
        return vacancyMapper.toDto(savedVacancy);
    }
}
