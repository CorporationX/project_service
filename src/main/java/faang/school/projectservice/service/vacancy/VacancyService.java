package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDtoForUpdate;
import faang.school.projectservice.exception.vacancy.VacancyValidateException;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.vacancy.VacancyValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;

import static faang.school.projectservice.commonMessages.vacancy.ErrorMessagesForVacancy.ERROR_OWNER_ROLE_FORMAT;
import static faang.school.projectservice.commonMessages.vacancy.ErrorMessagesForVacancy.PROJECT_NOT_EXIST_FORMAT;
import static faang.school.projectservice.commonMessages.vacancy.ErrorMessagesForVacancy.VACANCY_CANT_BE_CLOSED_FORMAT;
import static faang.school.projectservice.commonMessages.vacancy.ErrorMessagesForVacancy.VACANCY_NOT_EXIST_FORMAT;

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
    public VacancyDto createVacancy(VacancyDto vacancyDto) {
        // вакансия всегда относиться к какому-то проекту
        // -> перед созданием нужно проверить существует ли проект с таким ид
        // значит надо сходить в БД и проверить что такой проект существует
        // Также на проекте обязательно должен быть человек, ответственный за вакансию.
        // значит должен иметь какую-то определенную роль
        vacancyValidator.validateRequiredFieldsInDTO(vacancyDto);
        Project curProject = projectRepository.getProjectById(vacancyDto.getProjectId());
        checkOwnerVacancy(vacancyDto.getCreatedBy());

        Vacancy newVacancy = vacancyMapper.toEntity(vacancyDto);
        newVacancy.setProject(curProject);
        return vacancyMapper.toDto(vacancyRepository.save(newVacancy));
    }

    @Transactional
    public VacancyDto updateVacancy(VacancyDtoForUpdate vacancyDto) {
        vacancyValidator.validateRequiredFeildsForUpdateVacancy(vacancyDto);
        checkVacancyIsExist(vacancyDto.getVacancyId());
        // если я просто хочу закрыть вакансию, допустим что все ок
        // я сделаю update запрос с телом id вакансии, и статус = Close.
        // остальные параметры мне не очень интересны
        // должен ли я как то сопоставлять данные из БД с обновленными данными.
        // или условно клиент изначально послал запрос получил данные вакансии, потом поменял у себя что-то
        // апдейт сделлать когда?
        // в дто-шке есть два поля с ID, те их надо при апдейте игнорировать
        // т.е из ДТО получаем ИД проекта и ИД создателя, за этими сущностями идем в БД
        // обновляем эти сущности в вакансии
        // далее делаем апдейт, и если все ок, то должно сработать.

        // пытаемся получить вакансиию
        // сценарий если вакансии нет? должен ли я сохранять тогда, или выпасть по эксепшен
        Vacancy vacancyForUpdate = vacancyRepository.findById(vacancyDto.getVacancyId())
                .orElseThrow(() -> new VacancyValidateException(
                        MessageFormat.format(VACANCY_NOT_EXIST_FORMAT, vacancyDto.getVacancyId())));

        // проверка возможности закрытия вакансии
        if (isStatusClose(vacancyDto.getStatus())) {
            checkPossibilityCloseVacancy(vacancyForUpdate);
        }

        // проверяем владельца
        checkRoleUserForPossibilityUpdateVacancy(vacancyDto.getUpdatedBy());
        // готовы к обновлению
        vacancyMapper.updateEntityFromDto(vacancyDto, vacancyForUpdate);
        vacancyForUpdate.setUpdatedAt(LocalDateTime.now());

        return vacancyMapper.toDto(vacancyRepository.save(vacancyForUpdate));
    }

    private boolean isStatusClose(VacancyStatus status) {
        return status.equals(VacancyStatus.CLOSED);
    }

    private void checkPossibilityCloseVacancy(Vacancy vacancy) {
        if (vacancy.getCandidates().size() < MIN_COUNT_MEMBERS) {
            String errorMessage = MessageFormat.format(VACANCY_CANT_BE_CLOSED_FORMAT, vacancy.getId(), MIN_COUNT_MEMBERS);
            throw new VacancyValidateException(errorMessage);
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

    private void checkRoleUserForPossibilityUpdateVacancy(Long updatedBy) {
        TeamMember teamMember = teamMemberRepository.findById(updatedBy);
        List<TeamRole> roles = teamMember.getRoles();
        if (!roles.contains(TeamRole.OWNER) || !roles.contains(TeamRole.MANAGER)) {
            throw new VacancyValidateException("");
        }
    }
}
