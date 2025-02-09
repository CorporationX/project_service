package faang.school.projectservice.service.validator;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageUpdateDto;
import faang.school.projectservice.exception.BusinessException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.stage.StageTeamRoleFilter;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.StageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StageValidator {
    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private final ProjectService projectService;
    private final StageTeamRoleFilter stageTeamRoleFilter;

    public void validateStageCreation(StageDto stageDto) {
        /**
         * Проверки при создании этапа
         * Проверка наличия проекта:
         *
         * Убедиться, что проект, к которому привязывается этап, существует.
         *
         * Проверить, что проект не находится в закрытом состоянии (если этапы нельзя добавлять в закрытые проекты).
         *
         * Проверка названия этапа:
         *
         * Убедиться, что название этапа не пустое и соответствует допустимым требованиям (например, длина названия).
         *
         * Проверка списка ролей и количества участников:
         *
         * Убедиться, что список ролей не пуст.
         *
         * Проверить, что для каждой роли указано корректное количество участников (например, не отрицательное число).
         *
         * Убедиться, что указанные роли существуют в системе.
         *
         * Проверка прав доступа:
         *
         * Убедиться, что пользователь, создающий этап, имеет права на добавление этапов в проект (например, является менеджером или владельцем проекта).
         *
         * Проверка уникальности этапа в проекте:
         *
         * Убедиться, что этап с таким названием (или идентификатором) ещё не существует в рамках данного проекта (если требуется уникальность).
         */
        Project project = projectRepository.findById(stageDto.getProject().getId()).orElseThrow(
                () -> new EntityNotFoundException("Проект не найден.")
        );

        if (project.getStatus() == ProjectStatus.CANCELLED) {
            throw new BusinessException("Проект в статусе Отменен");
        }

    }

    public Project getValidProject(Long projectId) {
        return null;
    }

    public void checkStageForUpdate(Long stageId, StageUpdateDto stageUpdateDto) {
        /**
         * Проверки при обновлении этапа
         * Проверка существования этапа:
         *
         * Убедиться, что этап, который пытаются обновить, существует.
         *
         * Проверка прав доступа:
         *
         * Убедиться, что пользователь, обновляющий этап, имеет на это права (например, является менеджером или владельцем проекта).
         *
         * Проверка списка ролей и участников:
         *
         * Убедиться, что обновлённый список ролей и участников соответствует требованиям (например, для каждой роли указано корректное количество участников).
         *
         * Проверить, что все указанные роли существуют в системе.
         *
         * Проверка наличия участников с требуемыми ролями:
         *
         * Если на этап требуется участник с определённой ролью, убедиться, что в списке участников этапа есть пользователь с такой ролью.
         *
         * Если таких пользователей нет, проверить, что в проекте есть участники с требуемой ролью, и отправить им приглашения.
         *
         * Проверка изменений в названии этапа:
         *
         * Если изменяется название этапа, убедиться, что новое название соответствует требованиям (например, не пустое и не превышает допустимую длину).
         *
         * Проверка состояния проекта:
         *
         * Убедиться, что проект, к которому относится этап, не находится в состоянии, при котором обновление этапов запрещено (например, закрытый проект).
         */
    }

    public void checkStageToRemove(Long stageId) {
        /**
         * Проверка существования этапа:
         *
         * Убедиться, что этап, который пытаются удалить, существует.
         *
         * Проверка прав доступа:
         *
         * Убедиться, что пользователь, удаляющий этап, имеет на это права (например, является менеджером или владельцем проекта).
         *
         * Проверка связанных задач:
         *
         * Если задачи должны быть удалены каскадно, убедиться, что все связанные задачи удалены.
         *
         * Если задачи должны быть закрыты, проверить, что все задачи переведены в статус "закрыто".
         *
         * Если задачи должны быть перенесены в другой этап, убедиться, что целевой этап существует и задачи успешно перенесены.
         *
         * Проверка состояния проекта:
         *
         * Убедиться, что проект, к которому относится этап, не находится в состоянии, при котором удаление этапов запрещено (например, закрытый проект).
         **/
    }

    public void generalValidation(StageDto stageDto) {
        /**
         * Общие проверки для всех операций
         * Проверка корректности данных:
         *
         * Убедиться, что все передаваемые данные (например, ID проекта, ID этапа, список ролей) корректны и соответствуют ожидаемому формату.
         *
         * Проверка прав доступа:
         *
         * Для всех операций убедиться, что пользователь, выполняющий действие, имеет соответствующие права (например, является участником проекта с определённой ролью).
         *
         * Проверка состояния проекта:
         *
         * Убедиться, что проект, к которому относится этап, находится в состоянии, при котором допустимы операции с этапами (например, не закрыт).
         *
         * Проверка целостности данных:
         *
         * Убедиться, что после выполнения операции (создание, обновление, удаление) данные в БД остаются в согласованном состоянии (например, нет "висящих" задач после удаления этапа).
         */
    }
}
