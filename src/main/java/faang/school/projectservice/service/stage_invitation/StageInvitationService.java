package faang.school.projectservice.service.stage_invitation;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.service.stage.StageService;
import faang.school.projectservice.stage_invitation_filter.StageInvitationFilter;
import faang.school.projectservice.validator.stage_invitation.StageInvitationValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

import static faang.school.projectservice.model.stage_invitation.StageInvitationStatus.ACCEPTED;
import static faang.school.projectservice.model.stage_invitation.StageInvitationStatus.PENDING;
import static faang.school.projectservice.model.stage_invitation.StageInvitationStatus.REJECTED;

@Slf4j
@Service
@RequiredArgsConstructor
public class StageInvitationService {
    private StageService stageService;
    private StageInvitationMapper stageInvitationMapper;
    private List<StageInvitationFilter> stageInvitationFilters;
    private StageInvitationRepository stageInvitationRepository;
    private StageInvitationValidator stageInvitationValidator;

    public StageInvitationDto sendAnInvitation(StageInvitationDto stageInvitationDto) {
        stageInvitationValidator.checkWhetherThisRequestExists(stageInvitationDto.getId());
        stageInvitationValidator.checkTheExistenceOfTheInvitee(stageInvitationDto.getInvitedId());

        StageInvitation stageInvitation = stageInvitationMapper.toEntity(stageInvitationDto);
        stageInvitationRepository.save(stageInvitation);

        stageInvitationDto = StageInvitationDto.builder().status(PENDING).build();
        return stageInvitationDto;
    }

    public StageInvitationDto acceptAnInvitation(StageInvitationDto stageInvitationDto) {
        Long id = stageInvitationDto.getStageId();
        StageDto dto = stageService.getById(id);
        if (dto != null) {
            dto.getExecutorsId().add(id);
        }

        stageInvitationDto = StageInvitationDto.builder().status(ACCEPTED).build();
        return stageInvitationDto;
    }

    public StageInvitationDto rejectAnInvitation(StageInvitationDto stageInvitationDto) {
        stageInvitationValidator.checkTheReasonForTheFailure(stageInvitationDto.getRejection());
        stageInvitationDto = StageInvitationDto.builder().status(REJECTED).build();

        return stageInvitationDto;
    }

    public List<StageInvitationDto> viewAllInvitationsForOneParticipant(Long invitedId, StageInvitationFilterDto stageInvitationFilterDto) {
        Stream<StageInvitation> stageInvitations = stageInvitationRepository.findByInvitedUserId(invitedId).stream();

        if (stageInvitationFilterDto == null) {
            return stageInvitations.map(stageInvitationMapper::toDto).toList();
        }

        return stageInvitationFilters.stream()
                .filter(stageInvitationFilter -> stageInvitationFilter.isApplicable(stageInvitationFilterDto))
                .flatMap(stageInvitationFilter -> stageInvitationFilter.apply(stageInvitations, stageInvitationFilterDto))
                .map(stageInvitationMapper::toDto).toList();
    }
}
