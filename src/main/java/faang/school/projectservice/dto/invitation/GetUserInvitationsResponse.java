package faang.school.projectservice.dto.invitation;

import lombok.Builder;

import java.util.List;

@Builder
public record GetUserInvitationsResponse(
        List<InvitationDto> invitations
) {
}
