package faang.school.projectservice.repository;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import java.util.List;

import faang.school.projectservice.jpa.StageInvitationJpaRepository;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StageInvitationRepository {

    private final StageInvitationJpaRepository jpaRepository;

    public StageInvitation save(StageInvitation stageInvitation) {
        return jpaRepository.save(stageInvitation);
    }

    public Optional<StageInvitation> findById(Long id) {
        return jpaRepository.findById(id);
    }

    public boolean existsByStageAndInvited(Stage stage, TeamMember invited) {
        return jpaRepository.existsByStageAndInvited(stage, invited);
    }

    public List<StageInvitation> findByInvitedIdAndStatus(Long invitedId, StageInvitationStatus status) {
        return jpaRepository.findByInvitedIdAndStatus(invitedId, status);
    }

    public List<StageInvitation> findByInvitedId(Long invitedId) {
        return jpaRepository.findByInvitedId(invitedId);
    }

    public List<StageInvitation> findByStatus(StageInvitationStatus status) {
        return jpaRepository.findByStatus(status);
    }

    public List<StageInvitation> findAll() {
        return jpaRepository.findAll();
    }
}
