package faang.school.projectservice.filter;

import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class StageInvitationFilter {

    public Specification<StageInvitation> specificationStageInvitationByTeamMemberId(Long teamMemberId) {
        return (root, query, cb) ->
                cb.equal(root.get("teamMember").get("id"), teamMemberId);
    }

    public Specification<StageInvitation> specificationStatus(StageInvitationStatus status) {
        return (root, query, cb) ->
                cb.equal(root.get("status"), status);
    }

    public Specification<StageInvitation> specificationStageId(Long stageId) {
        return (root, query, cb) ->
                cb.equal(root.get("stage").get("id"), stageId);
    }
}