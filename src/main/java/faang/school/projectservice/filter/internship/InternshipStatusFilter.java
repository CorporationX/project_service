package faang.school.projectservice.filter.internship;

import faang.school.projectservice.dto.internship.InternshipFilterDto;
import org.springframework.stereotype.Component;
import faang.school.projectservice.model.Internship;

import java.util.stream.Stream;

@Component
public class InternshipStatusFilter implements InternshipFilter {

    @Override
    public boolean isApplicable(InternshipFilterDto internshipFilterDto) {
        return internshipFilterDto.getInternshipStatus() != null;
    }

    @Override
    public Stream<Internship> apply(Stream<Internship> internships, InternshipFilterDto internshipFilterDto) {
        return internships.filter(internship -> internship.getStatus().equals(internshipFilterDto.getInternshipStatus()));
    }
}
