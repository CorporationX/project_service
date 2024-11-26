package faang.school.projectservice.repository;

import java.util.List;

import faang.school.projectservice.jpa.StageInvitationJpaRepository;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StageInvitationRepository {
    private final StageInvitationJpaRepository repository;

    public void save(StageInvitation stageInvitation) {
        repository.save(stageInvitation);
    }

    public StageInvitation findById(Long stageInvitationId) {
        return repository.findById(stageInvitationId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Stage invitation doesn't exist by id: %s", stageInvitationId))
        );
    }

    public List<StageInvitation> findByInvitedUserId(Long userId) {
        return repository.findByInvited_UserId(userId);
    }

    public boolean stageInvitationExist(Long stageInvitationId) {
        return repository.existsById(stageInvitationId);
    }

    public List<StageInvitation> findAll() {
        return repository.findAll();
    }
}
