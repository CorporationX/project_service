package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageCreateDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.dto.stage.StageUpdateDto;
import faang.school.projectservice.dto.stage.StageViewDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.model.stage.enums.DeleteStrategy;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.stage.deletion.StageDeletionStrategy;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Сервис для управления этапами проекта.
 * <p>
 * Предоставляет методы для создания, обновления, удаления и получения информации о этапах проекта,
 * включая управление ролями участников и отправку приглашений.
 * </p>
 *
 * @author bozya
 * @since 31.07.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StageServiceImpl implements StageService {
    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private final StageMapper mapper;
    private final TeamMemberRepository teamMemberRepository;
    private final StageInvitationRepository invitationRepository;
    private final Map<DeleteStrategy, StageDeletionStrategy> strategies;


    /**
     * Создает новый этап проекта.
     *
     * @param stageCreateDto DTO с данными для создания этапа.
     * @return DTO созданного этапа.
     * @throws EntityNotFoundException если проект не найден.
     * @throws DataValidationException если данные этапа невалидны.
     */
    @Override
    @Transactional
    public StageViewDto create(StageCreateDto stageCreateDto) {
        validateCreateDto(stageCreateDto);

        Project project = projectRepository.findById(stageCreateDto.projectId())
                .orElseThrow(() -> new EntityNotFoundException("Проект с id" + stageCreateDto.projectId() + "не найден"));

        String stageNameDto = stageCreateDto.stageName();

        Stage newStage = Stage.builder()
                .stageName(stageNameDto)
                .project(project)
                .build();

        validateStageRolesDto(stageCreateDto, newStage);

        Stage savedStage = stageRepository.save(newStage);
        log.info("Этап {}, с id {} сохранен", savedStage.getStageName(), savedStage.getStageId());

        return mapper.toViewDto(savedStage);
    }


    /**
     * Возвращает список этапов проекта с применением фильтров.
     *
     * @param filtersDto DTO с параметрами фильтрации.
     * @return Список DTO этапов.
     */
    @Override
    public List<StageViewDto> getAllStagesWithFilters(StageFilterDto filtersDto) {
        List<Stage> filterResult = stageRepository.findByFilter(filtersDto);

        return filterResult.stream()
                .map(mapper::toViewDto)
                .toList();
    }

    /**
     * Удаляет этап проекта с использованием указанной стратегии.
     *
     * @param stageId ID этапа для удаления.
     * @param strategy Стратегия удаления.
     * @throws EntityNotFoundException если этап не найден.
     */
    @Override
    @Transactional
    public void delete(Long stageId, DeleteStrategy strategy) {
        Stage stage = stageRepository.findById(stageId)
                .orElseThrow(() -> new EntityNotFoundException("Стадия не найдена"));

        strategies.get(strategy).delete(stage);
    }

    /**
     * Обновляет данные этапа проекта.
     *
     * @param updateDto DTO с обновленными данными.
     * @param stageId ID обновляемого этапа.
     * @return DTO обновленного этапа.
     * @throws EntityNotFoundException если этап не найден.
     */
    @Override
    public StageViewDto update(StageUpdateDto updateDto, Long stageId) {
        Stage stage = stageRepository.findById(stageId)
                .orElseThrow(() -> new EntityNotFoundException("Этап" + stageId + "не найден"));

        updateDto(updateDto, stage);
        validateRolesAndMembers(stage);

        Stage updatedStage = stageRepository.save(stage);
        return mapper.toViewDto(updatedStage);

    }

    /**
     * Возвращает этап проекта по его ID.
     *
     * @param id ID этапа.
     * @return DTO запрошенного этапа.
     * @throws EntityNotFoundException если этап не найден.
     */
    @Override
    public StageViewDto getById(Long id) {
        Stage stage = stageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Такого этапа не существует"));

        return mapper.toViewDto(stage);
    }

    /**
     * Возвращает все этапы указанного проекта.
     *
     * @param id ID проекта.
     * @return Список DTO этапов проекта.
     * @throws EntityNotFoundException если проект не найден.
     */
    @Override
    public List<StageViewDto> getAllStages(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Такого проекта нет"));

        return project.getStages().stream()
                .map(mapper::toViewDto)
                .collect(Collectors.toList());
    }

    private void validateStageRolesDto(StageCreateDto stageCreateDto, Stage newStage) {
        if (stageCreateDto.stageRoles() != null && !stageCreateDto.stageRoles().isEmpty()) {
            List<StageRoles> stageRoles = stageCreateDto.stageRoles().stream()
                    .map(dto -> StageRoles.builder()
                            .teamRole(dto.role())
                            .count(dto.count())
                            .stage(newStage)
                            .build())
                    .toList();
            newStage.setStageRoles(stageRoles);
        }
    }

    private void validateCreateDto(StageCreateDto dto) {
        if (dto.stageName() == null || dto.stageName().isBlank()) {
            throw new DataValidationException("Имя этапа не может быть пустым");
        }
        if (dto.projectId() == null) {
            throw new DataValidationException("Id проекта не может быть пустым");
        }
    }

    private void updateDto(StageUpdateDto updateDto, Stage oldStage) {
        if (updateDto.stageName() != null) {
            oldStage.setStageName(updateDto.stageName());
        }

        if (updateDto.stageRoles() != null) {
            updateStageRoles(oldStage, updateDto.stageRoles());
        }

        if (updateDto.teamMemberIds() != null) {
            updateTeamMembers(oldStage, updateDto.teamMemberIds());
        }
    }

    private void updateStageRoles(Stage stage, List<StageRolesDto> newRoles) {
        stage.getStageRoles().clear();
        newRoles.forEach(roleDto -> {
            StageRoles role = StageRoles.builder()
                    .teamRole(roleDto.role())
                    .count(roleDto.count())
                    .stage(stage)
                    .build();
            stage.getStageRoles().add(role);
        });
    }
    private void updateTeamMembers(Stage stage, List<Long> newMemberIds) {
        stage.getExecutors().clear();
        newMemberIds.forEach(memberId -> {
            TeamMember member = teamMemberRepository.findById(memberId)
                    .orElseThrow(() -> new EntityNotFoundException("Team member not found"));
            stage.getExecutors().add(member);
        });
    }

    private void validateRolesAndMembers(Stage stage) {
        stage.getStageRoles().forEach(requiredRole -> {
            long currentCount = stage.getExecutors().stream()
                    .filter(member -> member.getRoles().contains(requiredRole.getTeamRole()))
                    .count();

            if (currentCount < requiredRole.getCount()) {
                findAndInviteMembers(stage, requiredRole);
            }
        });
    }

    private void findAndInviteMembers(Stage stage, StageRoles requiredRole) {
        TeamMember author = stage.getExecutors().stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("В этапе проекта должен быть" +
                        " хотя бы один участник для отправки приглашений"));

        List<TeamMember> projectMembers = teamMemberRepository.findByProjectId(stage.getProject().getId());

        List<TeamMember> candidates = projectMembers.stream()
                .filter(member -> member.getRoles().contains(requiredRole.getTeamRole()))
                .filter(member -> !stage.getExecutors().contains(member))
                .toList();

        long currentWithRole = countCurrentMembersWithRole(stage, requiredRole.getTeamRole());
        int needed = (int) (requiredRole.getCount() - currentWithRole);

        candidates.stream()
                .limit(needed)
                .forEach(member -> sendInvitation(member, stage, author));
    }

    private void sendInvitation(TeamMember invited, Stage stage, TeamMember author) {
        if (invitationRepository.existsByInvitedAndStage(invited, stage)) {
            log.warn("Пользователь {} уже получил приглашение для этапа {}", invited.getId(), stage.getStageName());
            return;
        }

        StageInvitation invitation = StageInvitation.builder()
                .stage(stage)
                .invited(invited)
                .author(author)
                .status(StageInvitationStatus.PENDING)
                .build();

        invitationRepository.save(invitation);
        log.info("Приглашение для {} было отправлено", invited.getId());
    }

    private long countCurrentMembersWithRole(Stage stage, TeamRole role) {
        return stage.getExecutors().stream()
                .filter(member -> member.getRoles().contains(role))
                .count();
    }
}