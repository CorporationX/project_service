package faang.school.projectservice.service.team_member;

import jakarta.persistence.EntityNotFoundException;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;

    public TeamMember findById(long teamMemberId) {
        return teamMemberRepository.findById(teamMemberId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Пользователь с ID %d не найден", teamMemberId)
                ));
    }
}
