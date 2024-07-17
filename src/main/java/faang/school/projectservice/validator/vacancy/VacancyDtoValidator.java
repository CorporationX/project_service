package faang.school.projectservice.validator.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import org.springframework.stereotype.Component;

@Component
public class VacancyDtoValidator {
    public boolean validVacancyDtoName(VacancyDto vacancyDto) {
        return vacancyDto.getName() != null;
    }

    public boolean validVacancyDtoDescription(VacancyDto vacancyDto) {
        return vacancyDto.getDescription() != null;
    }

    public boolean validVacancyDtoStatus(VacancyDto vacancyDto) {
        return vacancyDto.getStatus() != null;
    }

    public boolean validVacancyDtoSalary(VacancyDto vacancyDto) {
        return vacancyDto.getSalary() != null;
    }

    public boolean validVacancyDtoWorkSchedule(VacancyDto vacancyDto) {
        return vacancyDto.getWorkSchedule() != null;
    }

    public boolean validVacancyDtoRequiredSkillIds(VacancyDto vacancyDto) {
        return vacancyDto.getRequiredSkillIds() != null;
    }

    public boolean validVacancyDtoCandidatesIds(VacancyDto vacancyDto) {
        return vacancyDto.getCandidatesIds() != null;
    }
}
