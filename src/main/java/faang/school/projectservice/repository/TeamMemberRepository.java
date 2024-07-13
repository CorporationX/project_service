package faang.school.projectservice.repository;

import faang.school.projectservice.exception.ErrorMessage;
import faang.school.projectservice.exception.NotFoundException;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.TeamMember;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TeamMemberRepository {
    private final TeamMemberJpaRepository jpaRepository;

    public TeamMember findById(Long id) {
        return jpaRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Team member doesn't exist by id: %s", id)));
    }

    public void checkExistAll(Collection<Long> teamMemberIds) {
        if (jpaRepository.countAllByIds(teamMemberIds) == teamMemberIds.size()) {
            throw new NotFoundException(ErrorMessage.SOME_OF_MEMBERS_NOT_EXIST);
        }
    }

    public List<Long> findIdsByProjectIds(Collection<Long> projectIds) {
        return jpaRepository.findIdsByProjectIds(projectIds);
    }
}
