package faang.school.projectservice.service;

import faang.school.projectservice.model.Internship;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamMemberService {
    private final TeamMemberRepository teamMember;

    public void changeRoleForInterns(Internship internship) {

    }
}
