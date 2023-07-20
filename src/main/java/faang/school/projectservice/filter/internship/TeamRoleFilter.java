package faang.school.projectservice.filter.internship;

import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.model.Internship;

import java.util.stream.Stream;

public class TeamRoleFilter implements InternshipFilter {

    @Override
    public boolean isApplicable(InternshipFilterDto filterDto) {
        return filterDto.getInternshipRole() != null;
    }

    @Override
    public Stream<Internship> apply(Stream<Internship> internshipStream, InternshipFilterDto internshipFilterDto) {
        return internshipStream.filter(internship -> internship.getInternshipRole().equals(internshipFilterDto.getInternshipRole()));
    }
}
