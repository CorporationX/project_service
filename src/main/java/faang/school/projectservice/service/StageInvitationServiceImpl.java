package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.stageInvitation.StageInvitationAcceptDto;
import faang.school.projectservice.dto.stageInvitation.StageInvitationCreateDto;
import faang.school.projectservice.dto.stageInvitation.StageInvitationDeclineDto;
import faang.school.projectservice.dto.stageInvitation.StageInvitationDto;
import faang.school.projectservice.dto.stageInvitation.StageInvitationFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.filter.StageInvitationFilter;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class StageInvitationServiceImpl implements StageInvitationService {

    private final StageInvitationRepository stageInvitationRepository;
    private final StageInvitationMapper stageInvitationMapper;
    private final UserContext userContext;
    private final StageInvitationFilter stageInvitationFilter;

    @Transactional
    @Override
    public StageInvitationDto sendInvitation(StageInvitationCreateDto stageInvitationCreateDto) {
        validateUserId(stageInvitationCreateDto.author().getUserId());
        StageInvitation stageInvitation = stageInvitationMapper.toEntity(stageInvitationCreateDto);
        if (stageInvitationRepository.existsByAuthorAndInvitedAndStage(stageInvitation.getAuthor(),
                stageInvitation.getInvited(), stageInvitation.getStage())) {
            throw new DataValidationException("Приглашение уже было создано!");
        }
        stageInvitation.setStatus(StageInvitationStatus.PENDING);
        stageInvitationRepository.save(stageInvitation);
        return stageInvitationMapper.toDto(stageInvitation);
    }

    @Transactional
    @Override
    public void acceptInvitation(StageInvitationAcceptDto stageInvitationAcceptDto) {
        validateUserId(stageInvitationAcceptDto.idInvited());
        StageInvitation stageInvitation = stageInvitationByIdOrThrow(stageInvitationAcceptDto.idInvitation());
        if (!stageInvitationRepository.existsByAuthorAndInvitedAndStage(stageInvitation.getAuthor(),
                stageInvitation.getInvited(), stageInvitation.getStage())) {
            throw new EntityNotFoundException("Такого автора, приглашенного участника или этапа не существует");
        }
        stageInvitation.setStatus(StageInvitationStatus.ACCEPTED);
        stageInvitation.getStage().getExecutors().add(stageInvitation.getInvited());
    }

    @Transactional
    @Override
    public void declineInvitation(StageInvitationDeclineDto stageInvitationDeclineDto) {
        StageInvitation stageInvitation = stageInvitationByIdOrThrow(stageInvitationDeclineDto.stageInvitationId());
        validateUserId(stageInvitation.getInvited().getUserId());
        stageInvitation.setStatus(StageInvitationStatus.REJECTED);
        stageInvitation.setDescription(stageInvitationDeclineDto.description());
    }

    @Override
    public List<StageInvitationDto> viewAllInvitationsByFilter(StageInvitationFilterDto stageInvitationFilterDto) {
        long teamMemberId = stageInvitationFilterDto.teamMemberId();
        validateUserId(teamMemberId);
        StageInvitationStatus statusInvitation = stageInvitationFilterDto.status();
        long stageId = stageInvitationFilterDto.stageId();
        List<StageInvitation> stageInvitationList = stageInvitationRepository.findAllByInvited_Id(teamMemberId);
        stageInvitationList =
                stageInvitationFilter.invitationByStatusAndStage(stageInvitationList, statusInvitation, stageId);
        return stageInvitationMapper.toInvitationListDto(stageInvitationList);
    }

    private void validateUserId(long verifyUserId) {
        if (!Objects.equals(userContext.getUserId(), verifyUserId)) {
            log.warn("{} - Пользователь пытается изменить чужие данные, {} - id Оригинального пользователя",
                    userContext.getUserId(), verifyUserId);
            throw new ForbiddenException("Вы не можете изменять чужие данные!");
        }
    }

    private StageInvitation stageInvitationByIdOrThrow(long stageInvitationId) {
        return stageInvitationRepository.findById(stageInvitationId)
                .orElseThrow(() -> new EntityNotFoundException("Такого приглашения не существует!"));
    }
}