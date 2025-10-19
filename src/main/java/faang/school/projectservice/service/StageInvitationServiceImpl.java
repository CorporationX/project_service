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
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
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
    private final TeamMemberRepository teamMemberRepository;
    private final StageRepository stageRepository;
    private final StageInvitationMapper stageInvitationMapper;
    private final UserContext userContext;
    private final StageInvitationFilter stageInvitationFilter;

    @Override
    public StageInvitationDto sendInvitation(StageInvitationCreateDto stageInvitationCreateDto) {
        validateUserId(stageInvitationCreateDto.authorId());
        TeamMember author = teamMemberRepository.findByUserId(stageInvitationCreateDto.authorId());
        TeamMember invited = teamMemberRepository.findByUserId(stageInvitationCreateDto.invitedId());
        Stage stage = stageRepository.findById(stageInvitationCreateDto.stageId())
                .orElseThrow(() -> new EntityNotFoundException("Entity not found for this id"));
        String description = stageInvitationCreateDto.description() != null ?stageInvitationCreateDto.description() : " ";
        if (stageInvitationRepository.existsByAuthorAndInvitedAndStage(author, invited, stage)) {
            throw new DataValidationException("The invitation has already been created!");
        }
        StageInvitation stageInvitation = StageInvitation.builder()
                .description(description)
                .status(StageInvitationStatus.PENDING)
                .stage(stage)
                .author(author)
                .invited(invited)
                .build();
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

    @Transactional
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
        List<StageInvitation> stageInvitations = stageInvitationRepository.findAll(specification);
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