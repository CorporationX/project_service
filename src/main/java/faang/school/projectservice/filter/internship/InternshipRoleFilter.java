package faang.school.projectservice.filter.internship;

import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;

import java.util.stream.Stream;

public class InternshipRoleFilter implements InternshipFilter {
    private TeamMemberRepository teamMemberRepository;

    @Override
    public boolean isApplicable(InternshipFilterDto internshipFilterDto) {
        return internshipFilterDto.getId() > 0;
    }

    @Override
    public Stream<Internship> apply(Stream<Internship> internshipStream, InternshipFilterDto internshipFilterDto) {
        TeamMember teamMember = teamMemberRepository.findById(internshipFilterDto.getId());
        return internshipStream.filter(internship -> internship.getInterns().contains(teamMember));
    }
}
