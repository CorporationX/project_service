package faang.school.projectservice.validator;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.invitation.StageInvitationDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import java.util.stream.Stream;

@Component
public class Validator {
    private final TeamMemberRepository teamMemberRepository;
    private final UserContext userContext;

    public Validator(TeamMemberRepository teamMemberRepository, UserContext userContext) {
        this.teamMemberRepository = teamMemberRepository;
        this.userContext = userContext;
    }
    public void validateStageInvitationDto(@NotNull StageInvitationDto stageInvitationDto) {
        if (stageInvitationDto.getStage() == null || stageInvitationDto.getAuthor() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stage and Author must not be null");
        }

        StageInvitationStatus status = stageInvitationDto.getStatus();
        if (status != null && !isValidStageInvitationStatus(status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status value for StageInvitationDto");
        }
    }

    private boolean isValidStageInvitationStatus(StageInvitationStatus status) {
        return status == StageInvitationStatus.PENDING ||
                status == StageInvitationStatus.ACCEPTED ||
                status == StageInvitationStatus.REJECTED;
    }

    public void validateTeamMemberId(@NotNull @Positive Long teamMemberId) {
        try {
            teamMemberRepository.findById(teamMemberId);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team member with the provided id does not exist");
        }
    }

    public void validateAuthorId(@NotNull @Positive Long authorId) {
        try {
            teamMemberRepository.findById(authorId);
        } catch (EntityNotFoundException e) {
            throw new IllegalArgumentException("Author with the provided id does not exist");
        }
    }


    public void validateInvitationsFilterParams(Long teamMemberId, String status, Long authorId) {
        validateTeamMemberId(teamMemberId);

        Long currentUserId = userContext.getUserId();
        TeamMember currentUser = teamMemberRepository.findById(currentUserId);

        if (!currentUser.getId().equals(teamMemberId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to view invitations for this team member");
        }

        if (StringUtils.hasText(status) && !isValidStatus(status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status");
        }

        if (authorId != null) {
            try {
                teamMemberRepository.findById(authorId);
            } catch (EntityNotFoundException e) {
                throw new IllegalArgumentException("Invalid authorId");
            }
        }
    }

    private boolean isValidStatus(String status) {
        return Stream.of(StageInvitationStatus.values())
                .map(StageInvitationStatus::toString)
                .anyMatch(validStatus -> validStatus.equalsIgnoreCase(status));
    }
}
