/*
package faang.school.projectservice.repository;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.jpa.StageInvitationJpaRepository;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.service.filter.StageInvitationSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class StageInvitationRepository {
    private final StageInvitationJpaRepository repository;
    private final TeamMemberRepository teamMemberRepository;
    private final StageInvitationSpecification specification;

    public StageInvitation save(StageInvitation stageInvitation) {
        return repository.save(stageInvitation);
    }

    public StageInvitation findById(Long stageInvitationId) {
        return repository.findById(stageInvitationId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Stage invitation doesn't exist by id: %s", stageInvitationId))
        );
    }

    public List<StageInvitation> findByFilter(StageInvitationFilterDto filterDto) {
        Specification<StageInvitation> stageInvitationSpecification = specification.findByFilter(filterDto);
        return repository.findAll(stageInvitationSpecification)
                .stream()
                .skip((long) filterDto.getPage() * (long) filterDto.getPageSize())
                .limit(filterDto.getPageSize())
                .toList();
    }

    public boolean isInvitationExist(StageInvitationDto stageInvitationDto, Stage stage) {
        TeamMember author = teamMemberRepository.findById(stageInvitationDto.getAuthorId());
        TeamMember invited = teamMemberRepository.findById(stageInvitationDto.getInvitedId());
        return repository.existsByAuthorAndInvitedAndStage(author, invited, stage) ||
                repository.existsByInvitedAndStage(invited, stage);
    }
}
*/
