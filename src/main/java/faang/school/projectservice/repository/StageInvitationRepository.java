package faang.school.projectservice.repository;

import faang.school.projectservice.jpa.StageInvitationJpaRepository;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class StageInvitationRepository {
    private final StageInvitationJpaRepository repository;
    private final TeamMemberRepository teamMemberRepository;

    public StageInvitation save(StageInvitation stageInvitation) {
        return repository.save(stageInvitation);
    }

    public StageInvitation findById(Long stageInvitationId) throws EntityNotFoundException {
        return repository.findById(stageInvitationId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Stage invitation doesn't exist by id: %s", stageInvitationId))
        );
    }

    public boolean existsByAuthorAndInvitedAndStage(TeamMember author, TeamMember invited, Stage stage) {
        return repository.existsByAuthorAndInvitedAndStage(author, invited, stage);
    }

    public List<StageInvitation> findAll() {
        return repository.findAll();
    }
}
