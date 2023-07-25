package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.vacancy.VacancyValidateException;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.vacancy.VacancyValidator;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

import static faang.school.projectservice.commonMessages.vacancy.ErrorMessagesForVacancy.ERROR_OWNER_ROLE_FORMAT;
import static faang.school.projectservice.commonMessages.vacancy.ErrorMessagesForVacancy.PROJECT_NOT_EXIST_FORMAT;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final VacancyValidator vacancyValidator;

    @Transactional
    public VacancyDto createVacancy(@Valid VacancyDto vacancyDto) {
        // вакансия всегда относиться к какому-то проекту
        // -> перед созданием нужно проверить существует ли проект с таким ид
        // значит надо сходить в БД и проверить что такой проект существует
        // Также на проекте обязательно должен быть человек, ответственный за вакансию.
        // значит должен иметь какую-то определенную роль
        checkProjectIsExist(vacancyDto.getProjectId());
        checkOwnerVacancy(vacancyDto.getCreatedBy());
        Project curProject = projectRepository.getProjectById(vacancyDto.getProjectId());
        Vacancy newVacancy = vacancyMapper.toEntity(vacancyDto);
        newVacancy.setProject(curProject);
        Vacancy createdVacancy = vacancyRepository.save(newVacancy);
        return vacancyMapper.toDto(createdVacancy);
    }

    private void checkProjectIsExist(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            String errorMessage = MessageFormat.format(PROJECT_NOT_EXIST_FORMAT, projectId);
            throw new VacancyValidateException(errorMessage);
        }
    }

    private void checkOwnerVacancy(Long creatorId) {
        TeamMember owner = teamMemberRepository.findById(creatorId);
        if (!owner.getRoles().contains(TeamRole.OWNER)) {
            String errorMessage = MessageFormat.format(ERROR_OWNER_ROLE_FORMAT, creatorId);
            throw new VacancyValidateException(errorMessage);
        }
    }
}
