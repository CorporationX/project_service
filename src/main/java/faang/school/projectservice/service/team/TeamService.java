package faang.school.projectservice.service.team;

import faang.school.projectservice.dto.member.TeamMemberDto;
import faang.school.projectservice.mapper.teammember.TeamMemberMapper;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamMemberMapper memberMapper;
    private final TeamMemberRepository teamMemberRepository;

    public TeamMemberDto findMemberById(Long memberId) {
        return memberMapper.toDto(teamMemberRepository.findById(memberId));
    }
}
