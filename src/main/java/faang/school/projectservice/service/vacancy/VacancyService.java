package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.commonMessages.vacancy.ErrorMessagesForVacancy;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.vacancy.VacancyValidateException;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.vacancy.VacancyValidator;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

import static faang.school.projectservice.commonMessages.vacancy.ErrorMessagesForVacancy.*;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private static final int MIN_COUNT_MEMBERS = 5;
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
        vacancyValidator.validateRequiredFieldsInDTO(vacancyDto);
        checkProjectIsExist(vacancyDto.getProjectId());
        checkOwnerVacancy(vacancyDto.getCreatedBy());

        Project curProject = projectRepository.getProjectById(vacancyDto.getProjectId());
        Vacancy newVacancy = vacancyMapper.toEntity(vacancyDto);
        newVacancy.setProject(curProject);
        Vacancy createdVacancy = vacancyRepository.save(newVacancy);
        return vacancyMapper.toDto(createdVacancy);
    }

    @Transactional
    public VacancyDto updateVacancy(VacancyDto vacancyDto) {
        vacancyValidator.validateRequiredFeildsForUpdateVacancy(vacancyDto);
        checkVacancyIsExist(vacancyDto.getVacancyId());
        // если я просто хочу закрыть вакансию, допустим что все ок
        // я сделаю update запрос с телом id вакансии, и статус = Close.
        // остальные параметры мне не очень интересны
        // должен ли я как то сопоставлять данные из БД с обновленными данными.
        // или условно клиент изначально послал запрос получил данные вакансии, потом поменял у себя что-то

        if (isStatusClose(vacancyDto.getStatus())) {
            checkPossibilityCloseVacancy(vacancyDto);
        }

    }

    private boolean isStatusClose(VacancyStatus status) {
        return status.equals(VacancyStatus.CLOSED);
    }

    private void checkPossibilityCloseVacancy(VacancyDto vacancyDto) {
        Vacancy curVacancy = vacancyRepository.findById(vacancyDto.getVacancyId()).get();
        if (curVacancy.getCandidates().size() < MIN_COUNT_MEMBERS) {
            // вакансия не может быть переведена в статус закрыта, пока кол-во кандидатов меньше 5
        }
    }

    private Vacancy saveVacancy(Vacancy vacancyEntity) {
        return vacancyRepository.save(vacancyEntity);
    }

    private void checkProjectIsExist(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            String errorMessage = MessageFormat.format(PROJECT_NOT_EXIST_FORMAT, projectId);
            throw new VacancyValidateException(errorMessage);
        }
    }

    private void checkVacancyIsExist(Long vacancyId) {
        if (!vacancyRepository.existsById(vacancyId)) {
            String errorMessage = MessageFormat.format(VACANCY_NOT_EXIST_FORMAT, vacancyId);
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
