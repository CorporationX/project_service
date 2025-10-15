package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.vacancy.CreateVacancyDto;
import faang.school.projectservice.dto.vacancy.UpdateVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validation.vacancy.VacancyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VacancyServiceImpl implements VacancyService {
    final VacancyRepository vacancyRepository;
    final VacancyMapper vacancyMapper;
    final VacancyValidator vacancyValidation;
    final UserContext userContext;

    @Override
    public VacancyDto create(CreateVacancyDto vacancyDto) {
        vacancyValidation.validateCreate(userContext.getUserId(), vacancyDto.projectId());
        Vacancy vacancy = vacancyRepository.save(vacancyMapper.toVacancy(vacancyDto));
        return vacancyMapper.toVacancyDto(vacancy);
    }

    @Override
    public VacancyDto update(long vacancyId, UpdateVacancyDto vacancyDto) {
        Vacancy vacancy = vacancyRepository.getReferenceById(vacancyId);
        vacancyValidation.validateUpdate(userContext.getUserId(),
                vacancy,
                vacancyDto);
        vacancyMapper.update(vacancyDto, vacancy);
        vacancyRepository.save(vacancy);
        return vacancyMapper.toVacancyDto(vacancy);
    }

    @Override
    public List<VacancyDto> filterVacancies(TeamRole position, String vacancyName) {
        return vacancyRepository.findAll().stream()
                .filter(vacancy -> position != null && vacancy.getPosition() == position)
                .filter(vacancy -> vacancy.getName().toLowerCase().contains(vacancyName.toLowerCase()))
                .map(vacancyMapper::toVacancyDto)
                .toList();
    }

    @Override
    public VacancyDto getVacancyById(Long vacancyId) {
        Vacancy vacancy = vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new EntityNotFoundException("Vacancy not found"));
        return vacancyMapper.toVacancyDto(vacancy);
    }
}
