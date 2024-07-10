package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.vacancy.VacancyValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VacancyService {

    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;
    private final VacancyValidator vacancyValidator;

    @Autowired
    public VacancyService(VacancyRepository vacancyRepository, VacancyMapper vacancyMapper, VacancyValidator vacancyValidator) {
        this.vacancyRepository = vacancyRepository;
        this.vacancyMapper = vacancyMapper;
        this.vacancyValidator = vacancyValidator;
    }

    public VacancyDto createVacancy(VacancyDto vacancyDto) {
        vacancyValidator.createVacancyValidator(vacancyDto);
        Vacancy vacancy = vacancyMapper.toEntity(vacancyDto);
        vacancy = vacancyRepository.save(vacancy);
        return vacancyMapper.toDto(vacancy);
    }

    public VacancyDto updateVacancy(Long id, VacancyDto vacancyDto) {
        vacancyValidator.updateVacancyValidator(id, vacancyDto);
        Vacancy vacancy = vacancyRepository.getVacanciesById(id);
        updateVacancyFields(vacancy, vacancyDto);
        vacancy = vacancyRepository.save(vacancy);
        return vacancyMapper.toDto(vacancy);
    }

    private void updateVacancyFields(Vacancy vacancy, VacancyDto vacancyDto) {
        if (vacancyDto.getName() != null) {
            vacancy.setName(vacancyDto.getName());
        }
        if (vacancyDto.getDescription() != null) {
            vacancy.setDescription(vacancyDto.getDescription());
        }
        if (vacancyDto.getStatus() != null) {
            vacancy.setStatus(VacancyStatus.valueOf(vacancyDto.getStatus()));
        }
        if (vacancyDto.getSalary() != null) {
            vacancy.setSalary(vacancyDto.getSalary());
        }
        if (vacancyDto.getWorkSchedule() != null) {
            vacancy.setWorkSchedule(WorkSchedule.valueOf(vacancyDto.getWorkSchedule()));
        }
        if (vacancyDto.getRequiredSkillIds() != null) {
            vacancy.setRequiredSkillIds(vacancyDto.getRequiredSkillIds());
        }
    }
}
