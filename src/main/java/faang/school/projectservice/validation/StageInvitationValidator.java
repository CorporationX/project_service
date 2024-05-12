package faang.school.projectservice.validation;

import faang.school.projectservice.dto.client.StageInvitationDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.InvitationFilterDto;
import faang.school.projectservice.jpa.StageInvitationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StageInvitationValidator {
    private final StageInvitationJpaRepository stageInvitationJpaRepository;
    private final StageValidator stageValidator;

    public void createValidationController(StageInvitationDto stageInvitationDto) {
        if (stageInvitationDto.getStage() == null) {
            throw new DataValidationException("Передан пустой параметр этапа");
        }
        if (stageInvitationDto.getInvited() == null) {
            throw new DataValidationException("Передан пустой приглашенный");
        }
        if (stageInvitationDto.getAuthor() == null) {
            throw new DataValidationException("Передан пустой автор");
        }
    }

    public void acceptInvitationValidationController(StageInvitationDto stageInvitationDto) {
        if (stageInvitationDto.getStage() == null) {
            throw new DataValidationException("Передан пустой параметр этапа");
        }
        if (stageInvitationDto.getInvited() == null) {
            throw new DataValidationException("Передан пустой приглашенный");
        }
    }

    public void rejectInvitationValidationController(String explanation, StageInvitationDto stageInvitationDto) {
        if (explanation == null || explanation.isBlank()) {
            throw new DataValidationException("Передано пустое объяснение");
        }
        if (stageInvitationDto.getStage() == null) {
            throw new DataValidationException("Передан пустой параметр этапа");
        }
        if (stageInvitationDto.getInvited() == null) {
            throw new DataValidationException("Передан пустой приглашенный");
        }
    }

    public void showAllInvitationForMemberValidationController(Long userId, InvitationFilterDto invitationFilterDto) {
        if (userId == null) {
            throw new DataValidationException("Передан пустой приглашенный");
        }
        if (invitationFilterDto == null) {
            throw new DataValidationException("Передан пустой фильтр");
        }
    }

    public void createValidationService(StageInvitationDto stageInvitationDto) {
        if (stageInvitationJpaRepository.existsByInvitedAndStage(stageInvitationDto.getInvited(), stageInvitationDto.getStage())) {
            throw new DataValidationException("Невозможно выслать приглашение. Пользователь уже приглашен");
        }

        stageValidator.existsById(stageInvitationDto.getStage().getStageId());
    }

    public void acceptOrRejectInvitationService(StageInvitationDto stageInvitationDto) {
        if (stageInvitationJpaRepository.existsById(stageInvitationDto.getId())) {
            throw new DataValidationException("Не удалось найти приглашение с id: " + stageInvitationDto.getId());
        }
    }
}
