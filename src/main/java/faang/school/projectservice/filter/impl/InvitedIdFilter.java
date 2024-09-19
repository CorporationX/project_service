package faang.school.projectservice.filter.impl;

import faang.school.projectservice.filter.StageInvitationFilter;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class InvitedIdFilter implements StageInvitationFilter {
    private final Long invitedId;

    @Override
    public List<StageInvitation> apply(List<StageInvitation> invitations) {
        return invitations.stream()
                .filter(invitation -> invitation.getInvited() != null)
                .filter(invitation -> invitation.getInvited().getId().equals(invitedId))
                .collect(Collectors.toList());
    }
}