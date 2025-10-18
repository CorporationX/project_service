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
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
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

    @Override
    public StageInvitationDto sendInvitation(StageInvitationCreateDto stageInvitationCreateDto) {
        validateUserId(stageInvitationCreateDto.author().getUserId());
        StageInvitation stageInvitation = stageInvitationMapper.toEntity(stageInvitationCreateDto);
        if (stageInvitationRepository.existsByAuthorAndInvitedAndStage(stageInvitation.getAuthor(),
                stageInvitation.getInvited(), stageInvitation.getStage())) {
            throw new DataValidationException("The invitation has already been created!");
        }
        stageInvitation.setStatus(StageInvitationStatus.PENDING);
        stageInvitationRepository.save(stageInvitation);
        return stageInvitationMapper.toDto(stageInvitation);
    }

    @Transactional
    @Override
    public void acceptInvitation(StageInvitationAcceptDto stageInvitationAcceptDto) {
        validateUserId(stageInvitationAcceptDto.idInvited());
        StageInvitation stageInvitation = getStageInvitationByIdOrThrow(stageInvitationAcceptDto.idInvitation());
        if (!stageInvitationRepository.existsByAuthorAndInvitedAndStage(stageInvitation.getAuthor(),
                stageInvitation.getInvited(), stageInvitation.getStage())) {
            throw new EntityNotFoundException("This author, guest contributor or stage does not exist");
        }
        stageInvitation.setStatus(StageInvitationStatus.ACCEPTED);
        stageInvitation.getStage().getExecutors().add(stageInvitation.getInvited());
    }

    @Override
    public void declineInvitation(StageInvitationDeclineDto stageInvitationDeclineDto) {
        StageInvitation stageInvitation = getStageInvitationByIdOrThrow(stageInvitationDeclineDto.stageInvitationId());
        validateUserId(stageInvitation.getInvited().getUserId());
        stageInvitation.setStatus(StageInvitationStatus.REJECTED);
        stageInvitation.setDescription(stageInvitationDeclineDto.description());
        stageInvitationRepository.save(stageInvitation);
    }

    @Override
    public List<StageInvitationDto> viewAllInvitationsByFilter(StageInvitationFilterDto stageInvitationFilterDto) {
        long teamMemberId = stageInvitationFilterDto.teamMemberId();
        validateUserId(teamMemberId);
        Specification<StageInvitation> specification =
                stageInvitationFilter.specificationStageInvitationByTeamMemberId(teamMemberId);
        if (stageInvitationFilterDto.status() != null) {
            StageInvitationStatus status = stageInvitationFilterDto.status();
            specification = specification.and(stageInvitationFilter.specificationStatus(status));
        }
        if (stageInvitationFilterDto.stageId() != null) {
            long stageId = stageInvitationFilterDto.stageId();
            specification = specification.and(stageInvitationFilter.specificationStageId(stageId));
        }
        List<StageInvitation> stageInvitations =  stageInvitationRepository.findAll(specification);
        return stageInvitationMapper.toInvitationListDto(stageInvitations);
    }

    private void validateUserId(long verifyUserId) {
        if (!Objects.equals(userContext.getUserId(), verifyUserId)) {
            log.warn("{} - The user is trying to change someone else's data, {} - id Original user",
                    userContext.getUserId(), verifyUserId);
            throw new ForbiddenException("You cannot change someone else's data!");
        }
    }

    private StageInvitation getStageInvitationByIdOrThrow(long stageInvitationId) {
        return stageInvitationRepository.findById(stageInvitationId)
                .orElseThrow(() -> new EntityNotFoundException("There is no such invitation!"));
    }
}