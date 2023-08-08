package faang.school.projectservice.filter.internship;

import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class TeamRoleFilter implements InternshipFilter {

    @Override
    public boolean isApplicable(InternshipFilterDto filterDto) {
        return filterDto.getInternshipRole() != null;
    }

    @Override
    public Stream<Internship> apply(Stream<Internship> internshipStream, InternshipFilterDto internshipFilterDto) {
        return internshipStream.filter(internship -> internship.getInterns().stream()
                .allMatch(intern ->
                        intern.getRoles().contains(internshipFilterDto.getInternshipRole()
                        )
                )
        );
    }
}
