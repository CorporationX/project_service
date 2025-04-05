package faang.school.projectservice.service.teammember;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;

@Service
@RequiredArgsConstructor
public class TeamMemberServiceImpl implements TeamMemberService {

    private final UserContext userContext;
    private final TeamMemberRepository teamMemberRepository;

    @Override
    public TeamMember getCurrentTeamMember(long projectId) throws AccessDeniedException {
        long userId = userContext.getUserId();
        return teamMemberRepository.findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new AccessDeniedException("User is not the member of the project"));
    }
}
