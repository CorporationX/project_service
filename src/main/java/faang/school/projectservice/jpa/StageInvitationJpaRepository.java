package faang.school.projectservice.jpa;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StageInvitationJpaRepository extends JpaRepository<StageInvitation, Long> {
    List<StageInvitation> findByStage_StageIdAndAuthor_IdAndStatusAndId(Long stageId, Long authorId, StageInvitationStatus status, Long id);

    boolean existsByAuthorAndInvitedAndStage(TeamMember author, TeamMember invited, Stage stage);

    boolean existsByInvitedAndStage(TeamMember invited, Stage stage);
}
