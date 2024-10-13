package faang.school.projectservice.jpa;

import faang.school.projectservice.model.entity.TeamMember;
import faang.school.projectservice.model.entity.Stage;
import faang.school.projectservice.model.entity.StageInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface StageInvitationJpaRepository extends JpaRepository<StageInvitation, Long>, JpaSpecificationExecutor<StageInvitation> {

    boolean existsByAuthorAndInvitedAndStage(TeamMember author, TeamMember invited, Stage stage);

    boolean existsByInvitedAndStage(TeamMember invited, Stage stage);
}
