package faang.school.projectservice.filter.impl;

import faang.school.projectservice.filter.StageInvitationFilter;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class StatusFilter implements StageInvitationFilter {
    private final StageInvitationStatus status;

    @Override
    public List<StageInvitation> apply(List<StageInvitation> invitations) {
        return invitations.stream()
                .filter(invitation -> invitation.getStatus() == status)
                .collect(Collectors.toList());
    }
}