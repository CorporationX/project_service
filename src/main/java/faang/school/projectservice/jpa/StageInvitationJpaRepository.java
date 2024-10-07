package faang.school.projectservice.jpa;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface StageInvitationJpaRepository extends JpaRepository<StageInvitation, Long> {

    boolean existsByStageAndInvited(Stage stage, TeamMember invited);

    List<StageInvitation> findByInvitedIdAndStatus(Long invitedId, StageInvitationStatus status);

    List<StageInvitation> findByInvitedId(Long invitedId);

    List<StageInvitation> findByStatus(StageInvitationStatus status);
}