package faang.school.projectservice.service.utilservice;

import faang.school.projectservice.exception.NotFoundException;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.TeamMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamMemberUtilService {

    private final TeamMemberJpaRepository teamMemberJpaRepository;

    @Transactional(readOnly = true)
    public TeamMember getByUserIdAndProjectId(long userId, long projectId) {
        return teamMemberJpaRepository.findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        "Team member with user id=%d and project id=%d not found",
                        userId,
                        projectId))
                );
    }
}
