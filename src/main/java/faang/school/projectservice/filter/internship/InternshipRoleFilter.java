package faang.school.projectservice.filter.internship;

import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;
@Component
public class InternshipRoleFilter implements InternshipFilter {
    private TeamMemberRepository teamMemberRepository;

    @Override
    public boolean isApplicable(InternshipFilterDto internshipFilterDto) {
        return internshipFilterDto.getRole() != null;
    }

    @Override
    public Stream<Internship> apply(Stream<Internship> internshipStream, InternshipFilterDto internshipFilterDto) {
        return internshipStream.filter(internship -> internship.getInterns().contains(internshipFilterDto.getRole()));
     //  return internshipStream.filter(internship -> internship.getInterns().stream().anyMatch(intern -> intern.getRoles().contains(internshipFilterDto.getRole())));
    }
}
