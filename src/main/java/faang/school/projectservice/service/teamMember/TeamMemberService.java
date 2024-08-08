package faang.school.projectservice.service.teamMember;

import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.TeamMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamMemberService {
    private final TeamMemberJpaRepository repository;

    public TeamMember getOneByUserIdAndProjectIdOrThrow(long userId, long projectId) {
        return repository.findByUserIdAndProjectId(userId, projectId).orElseThrow();
    }
}
