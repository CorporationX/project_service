package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validation.vacancy.VacancyValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;
    private final VacancyValidator vacancyValidator;

    public VacancyDto create(VacancyDto vacancyDto) {
        vacancyValidator.validateIfProjectExistsById(vacancyDto.getProjectId());
        Vacancy savedVacancy = vacancyRepository.save(vacancyMapper.toEntity(vacancyDto));
        return vacancyMapper.toDto(savedVacancy);
    }

    /*public VacancyDto update(VacancyDto vacancyDto) {
        vacancyValidator.va
        long vacancyId = vacancyDto.getId();
        Vacancy vacancy = vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new EntityNotFoundException("Vacancy doesn't exist by id: " + vacancyId));
        if (vacancyDto.getStatus() == VacancyStatus.CLOSED) {

        }
    }*/
}
